/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.responses.ResumesResponse;
import com.barraiser.ats_integrations.lever.DTO.ResumeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class LeverResumeHandler {
	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;

	public List<ResumeDTO> getResumes(
			final String partnerId,
			final String opportunityId) throws Exception {
		log.info(String.format(
				"Fetching resumes from lever for opportunityId %s for partnerId %s",
				opportunityId,
				partnerId));

		return this.getResumesFromLever(
				partnerId,
				opportunityId)
				.getData();
	}

	private ResumesResponse getResumesFromLever(
			final String partnerId,
			final String opportunityId) throws Exception {
		try {
			final String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.getResumes(
							authorization,
							opportunityId)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to fetch resumes for opportunityId %s from lever for partnerId %s",
							opportunityId,
							partnerId),
					exception);

			throw exception;
		}
	}
}
