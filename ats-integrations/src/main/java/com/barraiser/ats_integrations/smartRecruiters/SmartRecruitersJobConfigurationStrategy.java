/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.common.ATSJobConfigurationStrategy;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.JobDTO;
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
public class SmartRecruitersJobConfigurationStrategy implements ATSJobConfigurationStrategy {
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final SmartRecruitersJobsFetcher smartRecruitersJobsFetcher;

	@Override
	public String atsProvider() {
		return ATSProvider.SMART_RECRUITERS.getValue();
	}

	@Override
	public AtsIntegration getAtsIntegrationData(PartnerATSIntegrationDAO partnerATSIntegrationDAO) throws Exception {
		final List<JobDTO> jobDTOs = this.smartRecruitersJobsFetcher
				.getJobs(partnerATSIntegrationDAO);

		return AtsIntegration
				.builder()
				.id(partnerATSIntegrationDAO.getAtsProvider())
				.name(partnerATSIntegrationDAO.getAtsProviderDisplayableName())
				.jobPostings(jobDTOs
						.stream()
						.map(jobDTO -> AtsJobPosting
								.builder()
								.id(jobDTO.getId())
								.name(jobDTO.getTitle())
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
