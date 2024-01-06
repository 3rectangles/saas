/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.merge.DTO.JobDTO;
import com.barraiser.ats_integrations.merge.responses.JobsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class MergeJobsFetcher {
	private final MergeATSClient mergeATSClient;
	private final MergeAccessManager mergeAccessManager;

	public List<JobDTO> getAllJobs(final PartnerATSIntegrationDAO partnerATSIntegrationDAO)
			throws Exception {
		log.info(String.format(
				"Fetching jobs from merge for partnerId:%s and ATS Provider:%s",
				partnerATSIntegrationDAO.getPartnerId(),
				partnerATSIntegrationDAO.getAtsProvider()));

		final List<JobDTO> jobDTOs = new ArrayList<>();

		JobsResponse response = this.getJobsFromMerge(
				partnerATSIntegrationDAO,
				null);

		jobDTOs.addAll(response.getResults());

		while (response.getNext() != null) {
			response = this.getJobsFromMerge(
					partnerATSIntegrationDAO,
					response.getNext());

			jobDTOs.addAll(response.getResults());
		}

		return jobDTOs;
	}

	public JobsResponse getJobsFromMerge(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String paginationCursor) throws Exception {
		try {
			final String authorization = this.mergeAccessManager
					.getAuthorizationHeader();

			final String xAccountToken = this.mergeAccessManager
					.getXAccountToken(partnerATSIntegrationDAO);

			if (paginationCursor == null) {
				return this.mergeATSClient
						.getJobs(
								authorization,
								xAccountToken)
						.getBody();
			}

			return this.mergeATSClient
					.getJobs(
							authorization,
							xAccountToken,
							paginationCursor)
					.getBody();
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to fetch from jobs from merge for partnerId:%s",
							partnerATSIntegrationDAO.getPartnerId()),
					exception);
			throw exception;
		}
	}
}
