/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.StageDTO;
import com.barraiser.ats_integrations.lever.responses.StageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class LeverStageHandler {
	private final LeverClient leverClient;
	private final LeverAccessManager leverAccessManager;

	public StageDTO getStage(
			final String stageId,
			final String partnerId) throws Exception {
		log.info(String.format(
				"Fetching stage with Id %s from lever from partnerId %s",
				stageId,
				partnerId));

		return this.getStageFromLever(
				stageId,
				partnerId)
				.getData();
	}

	private StageResponse getStageFromLever(
			final String stageId,
			final String partnerId) throws Exception {
		try {
			final String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.getStage(
							authorization,
							stageId)
					.getBody();
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to fetch stage from lever stageId:%s partnerId:%s",
							stageId,
							partnerId),
					exception);

			throw exception;
		}
	}
}
