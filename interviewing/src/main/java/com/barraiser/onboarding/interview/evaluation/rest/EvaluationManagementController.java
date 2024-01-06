/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.rest;

import com.barraiser.common.graphql.types.AddBulkEvaluationsResult;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.dto.evaluationManagement.AddEvaluationRequest;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.evaluation.add_evaluation.AddEvaluation;
import com.barraiser.onboarding.interview.evaluation.add_evaluation.AddEvaluationProcessingData;
import com.barraiser.onboarding.partner.rest.controllers.PartnerInfoController;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@RestController
@Log4j2
@AllArgsConstructor
public class EvaluationManagementController {

	private final AddEvaluation addEvaluation;
	private final JobRoleRepository jobRoleRepository;
	private final PhoneParser phoneParser;
	private final InterviewUtil interviewUtil;
	private final PartnerInfoController partnerInfoController;

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation")
	public ResponseEntity<Boolean> addEvaluation(@RequestBody final AddEvaluationRequest addEvaluationRequest) {

		final Boolean isAddedViaCalendarInterception = this.interviewUtil
				.isAddedViaCalInterception(addEvaluationRequest.getSource());

		final AddEvaluationProcessingData data = AddEvaluationProcessingData.builder()
				.evaluationId(addEvaluationRequest.getEvaluationId())
				.isCandidateAnonymous(addEvaluationRequest.getIsCandidateAnonymous())
				.candidateName(addEvaluationRequest.getCandidateName())
				.phone(addEvaluationRequest.getCandidatePhone() != null
						? this.phoneParser.getFormattedPhone(addEvaluationRequest.getCandidatePhone())
						: null)
				.email(addEvaluationRequest.getCandidateEmail())
				.workExperience(addEvaluationRequest.getWorkExperience())
				.jobRoleDAO(addEvaluationRequest.getJobRoleId() != null ? this.jobRoleRepository
						.findTopByEntityIdIdOrderByEntityIdVersionDesc(addEvaluationRequest.getJobRoleId()).get()
						: null

				)// TODO: Also check source
				.pocEmail(addEvaluationRequest.getPocEmails() != null
						? String.join(",", addEvaluationRequest.getPocEmails())
						: null) // TODO : Everything cant just be null by checking this.In normal interception
				// also we will end up setting things null
				.documentId(addEvaluationRequest.getResumeDocumentId())
				.documentLink(addEvaluationRequest.getResumeLink())
				.resumeUrl(addEvaluationRequest.getResumeLink())
				.isAtsEvaluation(addEvaluationRequest.getIsATSEvaluation())
				.isAddedViaCalendarInterception(isAddedViaCalendarInterception)
				.partnerId(addEvaluationRequest.getPartnerId())
				.shouldNameSplit(!this.partnerInfoController.isSaasTrialPartner(addEvaluationRequest.getPartnerId()))
				.build();

		data.setResult(AddBulkEvaluationsResult.builder().success(Boolean.TRUE).build());

		try {
			this.addEvaluation.add(data);
			if (!data.getResult().getSuccess()) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			log.error("Unable to add evaluation : ", e);
			throw e;
		}

		return ResponseEntity.ok(Boolean.TRUE);
	}

}
