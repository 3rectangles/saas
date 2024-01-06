/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.ats_integrations.dal.ATSToBRInterviewStructureMappingRepository;
import com.barraiser.common.graphql.input.UpdateCandidateInput;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.add_evaluation.ResumeUrlProcessor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class CandidateController {
	CandidateRepository candidateRepository;
	UserDetailsRepository userDetailsRepository;
	EvaluationRepository evaluationRepository;
	ResumeUrlProcessor resumeUrlProcessor;

	private final String BARRAISER_DEMO_USER_ID = "0693bf7f-d09a-42bb-8fe3-229468a3d3ba";

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/evaluation/{evaluationId}/getCandidate")
	ResponseEntity<String> getCandidateId(
			@PathVariable("evaluationId") final String evaluationId) {
		EvaluationDAO evaluation = this.evaluationRepository.findById(evaluationId).get();
		return ResponseEntity.ok(evaluation.getCandidateId());
	}

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/candidateUserDetails")
	ResponseEntity<String> updateCandidate(
			@RequestBody UpdateCandidateInput updateCandidateInput) {

		final CandidateDAO candidate = this.candidateRepository.findById(updateCandidateInput.getCandidateId()).get();
		String resumeUrl = "";
		// Adding Resume URL to candidate
		if (updateCandidateInput.getAtsSource() != null) {
			if (resumeUrl != null) {
				resumeUrl = this.getResumeUrl(updateCandidateInput.getResumeLink(),
						updateCandidateInput.getAtsSource());
			}
			candidateRepository.save(
					candidate.toBuilder()
							.resumeUrl(resumeUrl)
							.redactedResumeUrl(resumeUrl)
							.build());
		}

		if (candidate.getUserId() == null) {
			final String userId = UUID.randomUUID().toString();

			userDetailsRepository.save(
					UserDetailsDAO.builder()
							.id(userId)
							.firstName(updateCandidateInput.getFirstName())
							.lastName(updateCandidateInput.getLastName())
							.phone(updateCandidateInput.getPhoneNumber())
							.email(updateCandidateInput.getEmail())
							.resumeUrl(resumeUrl)
							.redactedResumeUrl(resumeUrl)
							.build());

			this.candidateRepository.save(candidate.toBuilder()
					.userId(userId).build());
		} else {
			UserDetailsDAO userDetailsDAO = this.userDetailsRepository.findById(candidate.getUserId()).get();
			userDetailsRepository.save(
					userDetailsDAO.toBuilder()
							.firstName(updateCandidateInput.getFirstName())
							.lastName(updateCandidateInput.getLastName())
							.phone(updateCandidateInput.getPhoneNumber())
							.email(updateCandidateInput.getEmail())
							.build());
		}
		// todo: add to cognito

		return ResponseEntity.ok(candidate.getId());
	}

	@SneakyThrows
	private String getResumeUrl(final String externalResumeLink, final String atsProvider) {

		String atsSource = atsProvider.startsWith("MERGE") ? "MERGE" : atsProvider;
		if (atsProvider.equals("MERGE_lever")) {
			atsSource = "LEVER";
		}

		return resumeUrlProcessor.getResumeUrl(BARRAISER_DEMO_USER_ID, null, externalResumeLink, atsSource);
	}
}
