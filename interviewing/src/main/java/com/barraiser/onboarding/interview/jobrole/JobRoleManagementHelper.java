/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.ats_integrations.dto.UpdateAtsJobRoleMappingDTO;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.commons.dto.jobRoleManagement.*;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.jobrole.dal.LocationDAO;
import com.barraiser.onboarding.interview.jobrole.dal.LocationRepository;
import com.barraiser.onboarding.interview.jobrole.dal.TeamDAO;
import com.barraiser.onboarding.interview.jobrole.dal.TeamRepository;
import com.barraiser.onboarding.jobRoleManagement.JobRole.search.JobRoleElasticsearchManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class JobRoleManagementHelper {

	private final ATSServiceClient atsServiceClient;
	private final TeamRepository teamRepository;
	private final LocationRepository locationRepository;

	public void updateAtsMappings(final JobRoleInput jobRole, final ATSProvider atsProvider, final String partnerId) {
		final UpdateAtsJobRoleMappingDTO.JobRoleMapping jobRoleMapping = UpdateAtsJobRoleMappingDTO.JobRoleMapping
				.builder()
				.jobRoleId(jobRole.getId())
				.atsJobPostingId(jobRole.getAtsId())
				.interviewStructureMappings(jobRole.getInterviewStructures() == null ? null
						: jobRole.getInterviewStructures().stream()
								.map(
										i -> UpdateAtsJobRoleMappingDTO.InterviewStructureMapping.builder()
												.interviewStructureId(i.getId())
												.atsInterviewStructureId(i.getAtsId())
												.build())
								.collect(Collectors.toList()))
				.build();
		final UpdateAtsJobRoleMappingDTO request = UpdateAtsJobRoleMappingDTO.builder()
				.partnerId(partnerId)
				.atsProvider(atsProvider)
				.jobRoleMappings(List.of(jobRoleMapping))
				.build();
		this.atsServiceClient.updateJobRoleMappings(request);
	}

	@Transactional
	public List<TeamDAO> createOrUpdateATSTeams(final List<Team> teamsInputs,
			final String partnerId,
			final String creationSource,
			final String creationSourceMeta) {
		List<TeamDAO> teamsToBeSaved = new ArrayList<>();
		for (Team teamInput : teamsInputs) {

			final TeamDAO team = this.teamRepository.findByAtsIdAndPartnerId(teamInput.getAtsId(), partnerId).orElse(
					TeamDAO.builder()
							.id(UUID.randomUUID().toString())
							.atsId(teamInput.getAtsId())
							.partnerId(partnerId)
							.build());

			final TeamDAO updatedTeamDAO = team.toBuilder()
					.description(teamInput.getDescription())
					.name(teamInput.getName())
					.creationSource(creationSource)
					.creationSourceMeta(creationSourceMeta)
					.build();

			teamsToBeSaved.add(updatedTeamDAO);
		}

		return this.teamRepository.saveAll(teamsToBeSaved);
	}

	@Transactional
	public List<LocationDAO> createOrUpdateATSLocations(final List<Location> locationsInputs,
			final String partnerId,
			final String creationSource,
			final String creationSourceMeta) {
		List<LocationDAO> locationsToBeSaved = new ArrayList<>();
		for (Location locationInput : locationsInputs) {

			final LocationDAO location = this.locationRepository
					.findByAtsIdAndPartnerId(locationInput.getAtsId(), partnerId).orElse(
							LocationDAO.builder()
									.id(UUID.randomUUID().toString())
									.atsId(locationInput.getAtsId())
									.partnerId(partnerId)
									.build());

			final LocationDAO updatedLocationDAO = location.toBuilder()
					.description(locationInput.getDescription())
					.name(locationInput.getName())
					.creationSource(creationSource)
					.creationSourceMeta(creationSourceMeta)
					.build();

			locationsToBeSaved.add(updatedLocationDAO);
		}

		return this.locationRepository.saveAll(locationsToBeSaved);
	}

}
