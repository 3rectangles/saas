/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.UpdatePartnerRolesRequestDTO;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.user.expert.ExpertDBManager;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerRepAdditionService {

	private final UserInformationManagementHelper userManagement;
	private final ExpertDBManager expertDBManager;
	private final ExpertElasticSearchManager expertElasticSearchManager;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final ExpertMapper expertMapper;
	private final PhoneParser phoneParser;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	private final static String PARTNER_SUPER_USER_ROLE_ID = "PARTNER_SUPER_ADMIN";

	public String addPartnerRep(final PartnerAccessInput partnerAccessInput) throws IOException {
		final String userId = this.addOrUpdateUser(partnerAccessInput);
		this.makePartnerRep(userId, partnerAccessInput.getPartnerId());
		this.makeExpert(userId, partnerAccessInput);
		this.makeSuperAdminForPartner(userId, partnerAccessInput.getPartnerId());
		return userId;
	}

	private void makeSuperAdminForPartner(final String userId, final String partnerId) {

		final UpdatePartnerRolesRequestDTO updatePartnerRolesRequestDTO = UpdatePartnerRolesRequestDTO.builder()
				.dimensionForWhichRolesAreToBeAdded(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeAdded(List.of(partnerId))
				.rolesToBeAdded(List.of(PARTNER_SUPER_USER_ROLE_ID))
				.build();

		this.authorizationServiceFeignClient.updatePartnerRoles(userId, updatePartnerRolesRequestDTO);
	}

	private String addOrUpdateUser(final PartnerAccessInput input) {
		final UserDetailsDAO user = this.userManagement.getOrCreateUserByEmail(input.getEmail());
		this.updateUserDetails(user, input);
		return user.getId();
	}

	private void updateUserDetails(final UserDetailsDAO user, final PartnerAccessInput input) {
		final String formattedPhone = this.phoneParser.getFormattedPhone(
				input.getPhone() == null ? user.getPhone() : input.getPhone());
		final String firstName = input.getFirstName() == null ? user.getFirstName() : input.getFirstName();
		final String lastName = input.getLastName() == null ? user.getLastName() : input.getLastName();
		this.userManagement.updateUserDetailsFromDAO(user.toBuilder()
				.phone(formattedPhone)
				.firstName(firstName)
				.lastName(lastName)
				.build());
		final String updatedPartnerIds = this.userManagement.getUpdatedUserPartnerId(user.getId(),
				input.getPartnerId());
		this.userManagement.updateUserAttributes(user.getId(), Map.of("custom:partnerId", updatedPartnerIds));
	}

	private void makePartnerRep(final String userId, final String partnerId) {
		this.userManagement.addUserRole(userId, UserRole.PARTNER);

		final Optional<PartnerRepsDAO> partnerRepsDAO = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId);
		if (partnerRepsDAO.isEmpty()) {
			this.partnerRepsRepository.save(PartnerRepsDAO.builder()
					.id(UUID.randomUUID().toString())
					.partnerRepId(userId)
					.partnerId(partnerId)
					.build());
		}
	}

	private void makeExpert(final String userId, final PartnerAccessInput input) throws IOException {
		final ExpertDAO expertDAO = this.expertDBManager.getOrCreateExpertById(userId);
		final ExpertDetails expertDetails = this.constructExpertDetails(expertDAO, input);
		this.expertDBManager.updateExpertDetails(expertDetails);
		this.expertElasticSearchManager.updateExpertDetails(expertDetails);
		this.userManagement.addUserRole(userId, UserRole.EXPERT);
	}

	private ExpertDetails constructExpertDetails(final ExpertDAO expertDAO, final PartnerAccessInput input) {
		return this.expertMapper.toExpertDetails(expertDAO).toBuilder()
				.isActive(Boolean.TRUE)
				.companiesForWhichExpertCanTakeInterview(this.getUpdatedCompaniesExpertCanTakeInterviewsFor(
						input.getPartnerId(), expertDAO.getCompaniesForWhichExpertCanTakeInterview()))
				.tenantId(input.getPartnerId())
				.build();
	}

	private List<String> getUpdatedCompaniesExpertCanTakeInterviewsFor(final String partnerId,
			final List<String> companiesExpertCanTakeInterviewsFor) {
		final String companyId = this.partnerCompanyRepository.findById(partnerId).get().getCompanyId();
		final List<String> updatedCompaniesForWhichExpertCanTakeInterviews = new ArrayList<>();

		if (companiesExpertCanTakeInterviewsFor != null) {
			updatedCompaniesForWhichExpertCanTakeInterviews.addAll(companiesExpertCanTakeInterviewsFor);
		}
		updatedCompaniesForWhichExpertCanTakeInterviews.add(companyId);
		return updatedCompaniesForWhichExpertCanTakeInterviews;
	}

}
