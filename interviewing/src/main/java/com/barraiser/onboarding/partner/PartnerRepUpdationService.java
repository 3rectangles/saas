/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.DeletePartnerRolesRequestDTO;
import com.barraiser.commons.dto.UpdatePartnerRolesRequestDTO;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PartnerRepUpdationService {

	private final PartnerRepsRepository partnerRepsRepository;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerExpertMaker partnerExpertMaker;

	@PersistenceContext
	private EntityManager entityManager;

	private final static String PARTNER_EXPERT_ROLE_ID = "PARTNER_EXPERT";

	public void updatePartnerRep(final String userId, final PartnerAccessInput partnerAccessInput,
			final String actor, final String creationSource, final String creationSourceMeta)
			throws IOException {

		final PartnerRepsDAO partnerRepBeforeUpdate = this.getPartnerRep(userId,
				partnerAccessInput.getPartnerId());

		/**
		 * This is to detach the entity from persistance context so that
		 * when another entity with same id is updated in the repository
		 * this entity does not get updated.
		 *
		 * Basically we are moving the entity from MANAGED to DETACHED state.
		 */
		this.entityManager.detach(partnerRepBeforeUpdate);

		this.updatePartnerRepInformation(partnerAccessInput, partnerRepBeforeUpdate, creationSource,
				creationSourceMeta);

		this.provideNecessaryAccess(userId, partnerRepBeforeUpdate, partnerAccessInput, actor);
	}

	private PartnerRepsDAO getPartnerRep(final String userId, final String partnerId) {
		return this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId).get();
	}

	private void updatePartnerRepInformation(final PartnerAccessInput partnerAccessInput,
			final PartnerRepsDAO partnerRepBeforeUpdate,
			final String creationSource,
			final String creationSourceMeta) {

		final String locations = partnerAccessInput.getLocations() != null
				? partnerAccessInput.getLocations().stream().collect(Collectors.joining(","))
				: "";

		final String teams = partnerAccessInput.getTeams() != null
				? partnerAccessInput.getTeams().stream().collect(Collectors.joining(","))
				: "";

		this.partnerRepsRepository.save(partnerRepBeforeUpdate.toBuilder()
				.locations(locations) // We are assuming one team , one location for now.
				.teams(teams)
				.partnerRoles(partnerAccessInput.getRoles())
				.creationSource(creationSource)
				.creationSourceMeta(creationSourceMeta)
				.build());
	}

	private void provideNecessaryAccess(final String userId, final PartnerRepsDAO partnerRepBeforeUpdate,
			final PartnerAccessInput partnerAccessInput, final String updatedBy) throws IOException {

		if ((partnerAccessInput.getRoles().contains(PARTNER_EXPERT_ROLE_ID))) {
			this.partnerExpertMaker.makeExpert(userId, partnerAccessInput);
		}

		this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
				.dimensionForWhichRolesAreToBeRemoved(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeRemoved(List.of(partnerAccessInput.getPartnerId()))
				.rolesToBeRemoved(partnerRepBeforeUpdate.getPartnerRoles())
				.dimensionForWhichRolesAreToBeAdded(Dimension.PARTNER)
				.dimensionValuesForWhichRolesAreToBeAdded(List.of(partnerAccessInput.getPartnerId()))
				.rolesToBeAdded(partnerAccessInput.getRoles())
				.updatedBy(updatedBy)
				.build());

		this.updateTeamlevelRoles(userId, partnerRepBeforeUpdate, partnerAccessInput, updatedBy);
		this.updateLocationLevelRoles(userId, partnerRepBeforeUpdate, partnerAccessInput, updatedBy);
	}

	private void updateTeamlevelRoles(final String userId, final PartnerRepsDAO partnerRepBeforeUpdate,
			final PartnerAccessInput partnerAccessInput, final String updatedBy) {

		if (partnerAccessInput.getTeams() != null && partnerAccessInput.getTeams().size() != 0) {
			this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
					.dimensionForWhichRolesAreToBeRemoved(Dimension.TEAM)
					.dimensionValuesForWhichRolesAreToBeRemoved(List.of(partnerRepBeforeUpdate.getTeams().split(",")))
					.rolesToBeRemoved(partnerRepBeforeUpdate.getPartnerRoles())
					.dimensionForWhichRolesAreToBeAdded(Dimension.TEAM)
					.dimensionValuesForWhichRolesAreToBeAdded(partnerAccessInput.getTeams())
					.rolesToBeAdded(partnerAccessInput.getRoles())
					.updatedBy(updatedBy)
					.build());

		} else {
			this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId,
					DeletePartnerRolesRequestDTO.builder()
							.dimension(Dimension.TEAM)
							.dimensionValues(List.of(partnerRepBeforeUpdate.getTeams().split(",")))
							.roleIds(partnerRepBeforeUpdate.getPartnerRoles())
							.updatedBy(updatedBy)
							.build());
		}
	}

	private void updateLocationLevelRoles(final String userId, final PartnerRepsDAO partnerRepBeforeUpdate,
			final PartnerAccessInput partnerAccessInput, final String updatedBy) {

		if (partnerAccessInput.getLocations() != null && partnerAccessInput.getLocations().size() != 0) {
			this.authorizationServiceFeignClient.updatePartnerRoles(userId, UpdatePartnerRolesRequestDTO.builder()
					.dimensionForWhichRolesAreToBeRemoved(Dimension.LOCATION)
					.dimensionValuesForWhichRolesAreToBeRemoved(
							List.of(partnerRepBeforeUpdate.getLocations().split(",")))
					.rolesToBeRemoved(partnerRepBeforeUpdate.getPartnerRoles())
					.dimensionForWhichRolesAreToBeAdded(Dimension.LOCATION)
					.dimensionValuesForWhichRolesAreToBeAdded(partnerAccessInput.getLocations())
					.rolesToBeAdded(partnerAccessInput.getRoles())
					.updatedBy(updatedBy)
					.build());

		} else {
			this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId,
					DeletePartnerRolesRequestDTO.builder()
							.dimension(Dimension.LOCATION)
							.dimensionValues(List.of(partnerRepBeforeUpdate.getLocations().split(",")))
							.roleIds(partnerRepBeforeUpdate.getPartnerRoles())
							.updatedBy(updatedBy)
							.build());
		}
	}

}
