/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.JobDTO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.JobsDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersJobsFetcher {
	private static final Integer LIMIT = 100;

	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;

	public List<JobDTO> getJobs(final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		log.info(String.format(
				"Fetching smart recruiters jobs for partnerId:%s",
				partnerATSIntegrationDAO.getPartnerId()));

		JobsDTO jobsDTO = this.getJobsFromSmartRecruiters(
				partnerATSIntegrationDAO,
				null);

		List<JobDTO> jobs = new ArrayList<>(jobsDTO.getContent());

		do {
			jobsDTO = this.getJobsFromSmartRecruiters(
					partnerATSIntegrationDAO,
					jobsDTO.getNextPageId());

			jobs.addAll(jobsDTO.getContent());
		} while (!jobsDTO.getNextPageId().equals(""));

		return jobs;
	}

	private JobsDTO getJobsFromSmartRecruiters(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String pageId) {
		try {
			final String apiKey = this.smartRecruitersAccessManager
					.getApiKey(partnerATSIntegrationDAO);

			if (pageId != null) {
				return this.smartRecruitersClient
						.getJobsOfCurrentPage(
								apiKey,
								pageId,
								LIMIT)
						.getBody();
			}

			return this.smartRecruitersClient
					.getJobs(
							apiKey,
							LIMIT)
					.getBody();
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to fetch jobs from Smart Recruiters for partnerId:%s",
							partnerATSIntegrationDAO.getPartnerId()),
					exception);

			throw exception;
		}
	}
}
