/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.UpdatePartnerRolesRequestDTO;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.user.UserAccessManagementEventGenerator;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PartnerRepresentativeAdditionService {

	private final UserInformationManagementHelper userManagement;
	private final PartnerRepsRepository partnerRepsRepository;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerExpertMaker partnerExpertCreator;
	private final UserAccessManagementEventGenerator userAccessManagementEventGenerator;

	private final static String PARTNER_EXPERT_ROLE_ID = "PARTNER_EXPERT";
	private final static String ATS_USER_ID = "ATS_USER";

	private static final String ATS_INTEGRATION_ACTOR = "ATS_INTEGRATION";

	public PartnerRepsDAO addPartnerRep(final String userId, final PartnerAccessInput partnerAccessInput,
			final String actor,
			final String creationSource, final String creationSourceMeta)
			throws IOException {

		final PartnerRepsDAO partnerRep = this.addPartnerRepInfo(userId, partnerAccessInput, creationSource,
				creationSourceMeta, actor);
		this.provideNecessaryAccess(userId, partnerAccessInput, actor, partnerRep);
		return partnerRep;
	}

	private PartnerRepsDAO addPartnerRepInfo(final String userId, final PartnerAccessInput partnerAccessInput,
			final String creationSource, final String creationSourceMeta, final String actor) {

		final String locations = partnerAccessInput.getLocations() != null
				? partnerAccessInput.getLocations().stream().collect(Collectors.joining(","))
				: "";

		final String teams = partnerAccessInput.getTeams() != null
				? partnerAccessInput.getTeams().stream().collect(Collectors.joining(","))
				: "";

		Optional<PartnerRepsDAO> savedPartnerRep = this.partnerRepsRepository.findByPartnerRepIdAndPartnerId(
				userId, partnerAccessInput.getPartnerId());

		if (savedPartnerRep.isPresent()) {
			return this.partnerRepsRepository.save(
					savedPartnerRep.get().toBuilder()
							.locations(locations)
							.teams(teams)
							.partnerRoles(partnerAccessInput.getRoles())
							.creationSource(creationSource)
							.creationSourceMeta(creationSourceMeta)
							.build());
		}

		if (actor.equals(ATS_INTEGRATION_ACTOR)) {
			this.userAccessManagementEventGenerator.sendUserAccessGrantedEvent(userId,
					partnerAccessInput.getPartnerId(), ATS_USER_ID);
		}

		return this.partnerRepsRepository.save(PartnerRepsDAO.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(partnerAccessInput.getPartnerId())
				.partnerRepId(userId)
				.locations(locations)
				.teams(teams)
				.partnerRoles(partnerAccessInput.getRoles())
				.creationSource(creationSource)
				.creationSourceMeta(creationSourceMeta)
				.build());
	}

	private void provideNecessaryAccess(final String userId,
			final PartnerAccessInput partnerAccessInput, final String updatedBy, final PartnerRepsDAO partnerRep)
			throws IOException {

		this.userManagement.addUserRole(userId, UserRole.PARTNER);

		if (partnerAccessInput.getRoles().contains(PARTNER_EXPERT_ROLE_ID)) {
			this.partnerExpertCreator.makeExpert(userId, partnerAccessInput);
		}

		this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
				.dimensionForWhichRolesAreToBeAdded(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeAdded(List.of(partnerAccessInput.getPartnerId()))
				.rolesToBeAdded(partnerAccessInput.getRoles())
				.updatedBy(updatedBy)
				.build());

		this.addTeamLevelRoles(userId, partnerAccessInput, updatedBy);
		this.addLocationLevelRoles(userId, partnerAccessInput, updatedBy);
	}

	private void addTeamLevelRoles(final String userId,
			final PartnerAccessInput partnerAccessInput, final String updatedBy) {

		if (partnerAccessInput.getTeams() != null && partnerAccessInput.getTeams().size() != 0) {
			this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
					.dimensionForWhichRolesAreToBeAdded(Dimension.TEAM)
					.dimensionValuesForWhichRolesAreToBeAdded(partnerAccessInput.getTeams()) // We are assuming one team
					// , one location for
					// now.
					.rolesToBeAdded(partnerAccessInput.getRoles())
					.updatedBy(updatedBy)
					.build());
		}

	}

	private void addLocationLevelRoles(final String userId,
			final PartnerAccessInput partnerAccessInput, final String updatedBy) {
		if (partnerAccessInput.getLocations() != null && partnerAccessInput.getLocations().size() != 0) {
			this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
					.dimensionForWhichRolesAreToBeAdded(Dimension.LOCATION)
					.dimensionValuesForWhichRolesAreToBeAdded(partnerAccessInput.getLocations())
					.rolesToBeAdded(partnerAccessInput.getRoles())
					.updatedBy(updatedBy)
					.build());
		}
	}

}
