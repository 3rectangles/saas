/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.*;
import com.barraiser.ats_integrations.lever.requests.LeverWebhookRequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class LeverCandidateStageChangeWebhookHandler {
	private static final String BARRAISER_STAGE = "BARRAISER SCREEN";

	private final LeverOpportunityHandler leverOpportunityHandler;
	private final LeverApplicationHandler leverApplicationHandler;
	private final LeverResumeHandler leverResumeHandler;
	private final LeverAddEvaluationEventCreator leverAddEvaluationEventCreator;
	private final LeverUserHandler leverUserHandler;
	private final LeverStageHandler leverStageHandler;

	public void addLeverOpportunityForEvaluation(
			final LeverWebhookRequestBody requestBody,
			final String partnerId)
			throws Exception {
		log.info("Adding lever application for evaluation");
		final StageDTO stageDTO = this.leverStageHandler
				.getStage(
						requestBody
								.getData()
								.getToStageId(),
						partnerId);

		if (!BARRAISER_STAGE.equalsIgnoreCase(stageDTO.getText())) {
			log.info(String.format(
					"Current stage not %s, it is %s on Lever",
					BARRAISER_STAGE,
					stageDTO.getText()));

			return;
		}

		final LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData = this
				.getLeverAddEvaluationEventCreationData(
						partnerId,
						requestBody);

		log.info(String.format(
				"Generating AddEvaluationEvent for lever opportunityId %s applicationId %s for partnerId %s",
				leverAddEvaluationEventCreationData
						.getOpportunityDTO()
						.getId(),
				leverAddEvaluationEventCreationData
						.getLeverApplicationDTO()
						.getId(),
				partnerId));

		this.leverAddEvaluationEventCreator
				.createAddEvaluationEventForLever(
						partnerId,
						leverAddEvaluationEventCreationData);
	}

	private LeverAddEvaluationEventCreationData getLeverAddEvaluationEventCreationData(
			final String partnerId,
			final LeverWebhookRequestBody requestBody) throws Exception {
		final OpportunityDTO opportunityDTO = this.leverOpportunityHandler
				.getOpportunity(
						requestBody
								.getData()
								.getOpportunityId(),
						partnerId);

		final LeverApplicationDTO leverApplicationDTO = this.leverApplicationHandler
				.getApplication(
						requestBody
								.getData()
								.getOpportunityId(),
						opportunityDTO
								.getApplications()
								.get(0),
						partnerId);

		final List<ResumeDTO> resumeDTOList = this.leverResumeHandler
				.getResumes(
						partnerId,
						requestBody
								.getData()
								.getOpportunityId());

		final UserDTO userDTO = this.leverUserHandler
				.getUser(
						leverApplicationDTO
								.getPostingOwner(),
						partnerId);

		LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData = new LeverAddEvaluationEventCreationData();

		leverAddEvaluationEventCreationData
				.setOpportunityDTO(opportunityDTO);

		leverAddEvaluationEventCreationData
				.setLeverApplicationDTO(leverApplicationDTO);

		leverAddEvaluationEventCreationData
				.setResumeDTOList(resumeDTOList);

		leverAddEvaluationEventCreationData
				.setUserDTO(userDTO);

		return leverAddEvaluationEventCreationData;
	}
}
