/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.endpoint;

import com.barraiser.ats_integrations.lever.LeverCandidateStageChangeWebhookHandler;
import com.barraiser.ats_integrations.lever.LeverInterviewWebhookHandler;
import com.barraiser.ats_integrations.lever.requests.LeverWebhookInterviewCreatedRequestBody;
import com.barraiser.ats_integrations.lever.requests.LeverWebhookRequestBody;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
public class LeverWebhookController {
	private static final String CANDIDATE_STAGE_CHANGE = "candidateStageChange";
	private static final String INTERVIEW_CREATED = "interviewCreated";

	private final LeverCandidateStageChangeWebhookHandler leverCandidateStageChangeWebhookHandler;
	private final LeverInterviewWebhookHandler leverInterviewWebhookHandler;

	@PostMapping(value = "/lever-webhook", consumes = "application/json")
	public void receiveLeverWebhook(
			@RequestParam("partner_id") final String partnerId,
			@RequestBody final LeverWebhookRequestBody requestBody)
			throws Exception {
		log.info(
				String.format(
						"Received lever webhook Id %s event %s partnerId:%s",
						requestBody.getId(),
						requestBody.getEvent(),
						partnerId));

		try {
			if (CANDIDATE_STAGE_CHANGE
					.equals(requestBody.getEvent())) {
				this.leverCandidateStageChangeWebhookHandler
						.addLeverOpportunityForEvaluation(
								requestBody,
								partnerId);
			}
		} catch (Exception exception) {
			log.warn("Lever Webhook failed : ", exception);
		}
	}

	/**
	 * TODO: Check that this interiew is only processed for a saas client.
	 * For now we will configure this weebhook only for a saas client hence
	 * shouldn;t be an
	 * issue.
	 */
	@PostMapping(value = "/lever-webhook/interview/created", consumes = "application/json")
	public void receiveLeverInterviewsWebhook(
			@RequestParam("partner_id") final String partnerId,
			@RequestBody final LeverWebhookInterviewCreatedRequestBody requestBody)
			throws Exception {
		log.info(
				String.format(
						"Received lever webhook Id %s event %s partnerId:%s",
						requestBody.getId(),
						requestBody.getEvent(),
						partnerId));

		try {
			if (INTERVIEW_CREATED
					.equals(requestBody.getEvent())) {
				this.leverInterviewWebhookHandler
						.handleInterviewCreation(
								requestBody,
								partnerId);
			}
		} catch (Exception exception) {
			log.warn("Lever Interview Webhook failed : ", exception);
		}
	}
}
