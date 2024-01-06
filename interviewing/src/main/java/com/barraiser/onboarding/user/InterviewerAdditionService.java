/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.UpdatePartnerRolesRequestDTO;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.partner.PartnerExpertMaker;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewerAdditionService {
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerExpertMaker partnerExpertMaker;
	private final UserAccessManagementEventGenerator userAccessManagementEventGenerator;

	private final static String PARTNER_EXPERT_ROLE_ID = "PARTNER_EXPERT";

	private final static String ATS_USER_ID = "ATS_USER";

	public String addInterviewer(final PartnerAccessInput partnerAccessInput) {

		// 1. Check if User is present with this email address - if not create
		final UserDetailsDAO user = this.userInformationManagementHelper
				.getOrCreateUserByEmail(partnerAccessInput.getEmail());
		// 2. Check if partner rep is present for this partner and userId - if not
		// create
		final PartnerRepsDAO partnerRepsDAO = this.getOrCreatePartnerRep(user.getId(),
				partnerAccessInput.getPartnerId());

		this.updateUserDetails(user, partnerAccessInput);
		// 3. Check if role is present for this partner rep
		this.authorizationServiceFeignClient.updatePartnerRoles(user.getId(), UpdatePartnerRolesRequestDTO.builder()
				.dimensionForWhichRolesAreToBeAdded(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeAdded(List.of(partnerAccessInput.getPartnerId()))
				.dimensionForWhichRolesAreToBeRemoved(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeRemoved(List.of(partnerAccessInput.getPartnerId()))
				.rolesToBeRemoved(List.of(PARTNER_EXPERT_ROLE_ID))
				.rolesToBeAdded(List.of(PARTNER_EXPERT_ROLE_ID))
				.build());
		// 4. Also add in partner reps table roles
		this.addInterviewerRoleInPartnerReps(partnerRepsDAO);

		// Add Roles in cognito
		try {
			this.partnerExpertMaker.makeExpert(user.getId(), partnerAccessInput);

			this.userInformationManagementHelper.addUserRole(user.getId(), UserRole.PARTNER);
		} catch (IOException E) {
			log.info("Failure at creating expert with email - " + partnerAccessInput.getEmail());
		}

		return user.getId();
	}

	private PartnerRepsDAO getOrCreatePartnerRep(final String userId, final String partnerId) {
		final Optional<PartnerRepsDAO> partnerRepsDAO = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId);

		if (partnerRepsDAO.isEmpty()) {

			this.userAccessManagementEventGenerator.sendUserAccessGrantedEvent(userId,
					partnerId, ATS_USER_ID);

			return this.partnerRepsRepository.save(PartnerRepsDAO.builder()
					.id(UUID.randomUUID().toString())
					.partnerRepId(userId)
					.partnerId(partnerId)
					.build());
		}

		return partnerRepsDAO.get();
	}

	private void updateUserDetails(final UserDetailsDAO user, final PartnerAccessInput input) {
		this.userInformationManagementHelper.updateUserDetailsFromDAO(user.toBuilder()
				.firstName(user.getFirstName() == null ? input.getFirstName() : user.getFirstName())
				.lastName(user.getLastName() == null ? input.getLastName() : user.getLastName())
				.build());

		// Update in Cognito
		final String updatedPartnerIds = this.userInformationManagementHelper.getUpdatedUserPartnerId(user.getId(),
				input.getPartnerId());

		this.userInformationManagementHelper.updateUserAttributes(user.getId(),
				Map.of("custom:partnerId", updatedPartnerIds));
	}

	private void addInterviewerRoleInPartnerReps(PartnerRepsDAO partnerRepsDAO) {
		List<String> roles = partnerRepsDAO.getPartnerRoles();

		if (roles == null)
			roles = new ArrayList<>();

		if (!roles.contains(PARTNER_EXPERT_ROLE_ID)) {
			roles.add(PARTNER_EXPERT_ROLE_ID);
		}

		this.partnerRepsRepository.save(
				partnerRepsDAO.toBuilder()
						.partnerRoles(roles)
						.build());
	}
}
