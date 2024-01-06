/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.common.ATSJobConfigurationStrategy;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.merge.DTO.JobDTO;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.types.AtsIntegration;
import com.barraiser.common.graphql.types.AtsJobPosting;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class MergeJobConfigurationStrategy implements ATSJobConfigurationStrategy {
	private final MergeJobsFetcher mergeJobsFetcher;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;

	@Override
	public String atsProvider() {
		return ATSProvider.MERGE.getValue();
	}

	@Override
	public AtsIntegration getAtsIntegrationData(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO)
			throws Exception {
		final List<JobDTO> jobDTOs = this.mergeJobsFetcher
				.getAllJobs(partnerATSIntegrationDAO);

		return AtsIntegration
				.builder()
				.id(partnerATSIntegrationDAO.getAtsProvider())
				.name(partnerATSIntegrationDAO.getAtsProviderDisplayableName())
				.jobPostings(jobDTOs
						.stream()
						.map(jobDTO -> AtsJobPosting
								.builder()
								.id(jobDTO.getId())
								.name(jobDTO.getName())
								.jobRoleId(this.getAttachedJobRoleId(
										partnerATSIntegrationDAO,
										jobDTO.getId()))
								.build())
						.collect(Collectors.toList()))
				.build();
	}

	private String getAttachedJobRoleId(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String atsJobPostingId) {
		Optional<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAO = this.atsJobPostingToBRJobRoleRepository
				.findByAtsJobPostingIdAndAtsProvider(
						atsJobPostingId,
						partnerATSIntegrationDAO.getAtsProvider());

		return atsJobPostingToBRJobRoleDAO
				.map(ATSJobPostingToBRJobRoleDAO::getBrJobRoleId)
				.orElse(null);

	}
}
