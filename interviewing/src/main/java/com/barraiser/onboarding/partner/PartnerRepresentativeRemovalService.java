/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.DeletePartnerRolesRequestDTO;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.onboarding.user.expert.ExpertDBManager;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class PartnerRepresentativeRemovalService {

	private final UserInformationManagementHelper userManagement;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	private final ExpertElasticSearchManager expertElasticSearchManager;
	private final ExpertDBManager expertDBManager;
	private final ExpertMapper expertMapper;

	@PersistenceContext
	private EntityManager entityManager;

	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	public void removeAsPartnerRep(final AuthenticatedUser actor, final String userId, final String partnerId)
			throws IOException {
		final Optional<PartnerRepsDAO> partnerRepBeforeRemoval = this.getPartnerRep(userId, partnerId);

		/**
		 * This is to detach the entity from persistance context so that
		 * when another entity with same id is updated in the repository
		 * this entity does not get updated.
		 *
		 * Basically we are moving the entity from MANAGED to DETACHED state.
		 */
		if (!partnerRepBeforeRemoval.isEmpty()) {
			this.entityManager.detach(partnerRepBeforeRemoval.get());
		}

		this.removeAccessAsPartnerRepForPartner(userId, partnerId, partnerRepBeforeRemoval);
		this.removeAccessAsExpertForPartner(userId, partnerId);
		this.removeUserRoles(actor, userId, partnerId, partnerRepBeforeRemoval);
	}

	private Optional<PartnerRepsDAO> getPartnerRep(final String userId, final String partnerId) {
		return this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId);
	}

	private void removeAccessAsPartnerRepForPartner(final String userId, final String partnerId,
			final Optional<PartnerRepsDAO> partnerRepsDAO) {
		if (partnerRepsDAO.isPresent()) {
			this.partnerRepsRepository.deleteById(partnerRepsDAO.get().getId());
		}
		final String partnerIds = this.userManagement.getUserAttributes(userId).getOrDefault("custom:partnerId", "");
		final String updatedPartnerIds = Arrays.stream(partnerIds.split(",")).filter(x -> !partnerId.equals(x))
				.collect(Collectors.joining(","));
		this.userManagement.updateUserAttributes(userId, Map.of("custom:partnerId", updatedPartnerIds));
	}

	private void removeAccessAsExpertForPartner(final String userId, final String partnerId) throws IOException {
		final ExpertDAO expertDAO = this.expertDBManager.getExpert(userId);
		if (expertDAO != null) {
			final ExpertDetails expertDetails = this.constructExpertDetails(expertDAO, partnerId);
			this.expertDBManager.updateExpertDetails(expertDetails);
			this.expertElasticSearchManager.updateExpertDetails(expertDetails);
		}
	}

	private ExpertDetails constructExpertDetails(final ExpertDAO expertDAO, final String partnerId) {
		return this.expertMapper.toExpertDetails(expertDAO).toBuilder()
				.companiesForWhichExpertCanTakeInterview(this.getUpdatedCompaniesExpertCanTakeInterviewsFor(
						partnerId, expertDAO.getCompaniesForWhichExpertCanTakeInterview()))
				.build();
	}

	private List<String> getUpdatedCompaniesExpertCanTakeInterviewsFor(final String partnerId,
			final List<String> companiesExpertCanTakeInterviewsFor) {
		final String companyId = this.partnerCompanyRepository.findById(partnerId).get().getCompanyId();
		final List<String> updatedCompaniesForWhichExpertCanTakeInterviews = new ArrayList<>();

		if (companiesExpertCanTakeInterviewsFor != null) {
			updatedCompaniesForWhichExpertCanTakeInterviews.addAll(companiesExpertCanTakeInterviewsFor);
		}
		updatedCompaniesForWhichExpertCanTakeInterviews.remove(companyId);
		return updatedCompaniesForWhichExpertCanTakeInterviews;
	}

	private void removeUserRoles(final AuthenticatedUser actor, final String userId, final String partnerId,
			final Optional<PartnerRepsDAO> partnerRepBeforeRemoval) {
		this.removeRolesOnCognito(userId);
		this.removeRolesInAuthorizationService(userId, partnerId, partnerRepBeforeRemoval, actor.getUserName());
	}

	private void removeRolesOnCognito(final String userId) {
		this.userManagement.removeAllUserRoles(userId);
	}

	private void removeRolesInAuthorizationService(final String userId, final String partnerId,
			final Optional<PartnerRepsDAO> partnerRepBeforeRemoval, final String updatedBy) {

		if (!partnerRepBeforeRemoval.isEmpty()) {

			this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId, DeletePartnerRolesRequestDTO.builder()
					.dimension(Dimension.PARTNER)
					.dimensionValues(List.of(partnerId))
					.roleIds(partnerRepBeforeRemoval.get().getPartnerRoles())
					.updatedBy(updatedBy)
					.build());

			if (partnerRepBeforeRemoval.get().getTeams() != null) {
				this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId,
						DeletePartnerRolesRequestDTO.builder()
								.dimension(Dimension.TEAM)
								.dimensionValues(List.of(partnerRepBeforeRemoval.get().getTeams().split(",")))
								.roleIds(partnerRepBeforeRemoval.get().getPartnerRoles())
								.updatedBy(updatedBy)
								.build());
			}

			if (partnerRepBeforeRemoval.get().getLocations() != null) {
				this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId,
						DeletePartnerRolesRequestDTO.builder()
								.dimension(Dimension.LOCATION)
								.dimensionValues(List.of(partnerRepBeforeRemoval.get().getLocations().split(",")))
								.roleIds(partnerRepBeforeRemoval.get().getPartnerRoles())
								.updatedBy(updatedBy)
								.build());
			}
		}
	}
}
