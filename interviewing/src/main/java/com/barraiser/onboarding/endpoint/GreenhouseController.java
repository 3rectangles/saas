/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.amazonaws.services.securityhub.model.InvalidAccessException;
import com.barraiser.common.security.DataSecurityManager;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.atserrorevent.ATSErrorEvent;
import com.barraiser.onboarding.ats_integrations.greenhouse.*;
import com.barraiser.onboarding.auth.apikey.ApiKeyDAO;
import com.barraiser.onboarding.auth.apikey.ApiKeyRepository;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.graphql.GenerateEventMutation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Log4j2
@AllArgsConstructor
public class GreenhouseController {
	private static final String ATS_ERROR_EVENT = "ATSErrorEvent";

	private final JobRoleRepository jobRoleRepository;
	private final EvaluationRepository evaluationRepository;
	private final ApiKeyRepository apiKeyRepository;
	private final GreenhouseListTestRequestHandler greenhouseListTestRequestHandler;
	private final GreenhouseSendTestRequestHandler greenhouseSendTestRequestHandler;
	private final GreenhouseTestStatusRequestHandler greenhouseTestStatusRequestHandler;
	private final UserDetailsRepository userDetailsRepository;
	private final GenerateEventMutation generateEventMutation;

	@GetMapping(path = "/list_tests", produces = "application/json")
	public ResponseEntity<List<GreenhouseTest>> listTestsHandler(
			@RequestHeader(name = "Authorization") String token) throws Exception {
		final ApiKeyDAO apiKeyDAO = this.getApiKeyDAO(token);

		String partnerId = apiKeyDAO.getPartnerId();

		log.info(String.format(
				"greenhouse list_tests endpoint for partnerId : %s called",
				partnerId));

		List<GreenhouseTest> greenhouseTestList;
		try {
			greenhouseTestList = this.greenhouseListTestRequestHandler.getGreenHouseTests(partnerId);
		} catch (Exception exception) {
			log.error("Unable to fetch greenhouse tests", exception);

			throw exception;
		}

		return ResponseEntity.ok().body(greenhouseTestList);
	}

	@PostMapping(value = "/send_test", produces = "application/json", consumes = "application/json")
	public GreenhouseSendTestResponseBody sendTestHandler(
			@RequestHeader(name = "Authorization") String token,
			@RequestBody final GreenhouseSendTestRequestBody sendTestRequestBody) throws Exception {
		final ApiKeyDAO apiKeyDAO = this.getApiKeyDAO(token);

		final JobRoleDAO jobRoleDAO = this.jobRoleRepository.findTopByEntityIdIdOrderByEntityIdVersionDesc(
				sendTestRequestBody.getPartnerTestId())
				.orElseThrow(
						() -> new IllegalArgumentException("invalid partner_test_id"));
		UserDetailsDAO userDetailsDAO = this.userDetailsRepository
				.findById(apiKeyDAO.getUserId()).get();

		log.info(String.format(
				"Greenhouse send_test API for jobRoledId : %s by UserId : %s called",
				jobRoleDAO.getEntityId().getId(),
				userDetailsDAO.getId()));

		String evaluationId = null;
		try {
			if (this.greenhouseSendTestRequestHandler
					.checkMandatoryRequirements(
							apiKeyDAO,
							sendTestRequestBody)) {
				evaluationId = this.greenhouseSendTestRequestHandler
						.addCandidateForEvaluation(
								sendTestRequestBody,
								jobRoleDAO,
								userDetailsDAO,
								apiKeyDAO);
			} else {
				final ATSErrorEvent atsErrorEvent = this.greenhouseSendTestRequestHandler
						.constructATSErrorEvent(
								sendTestRequestBody,
								apiKeyDAO);

				this.generateEventMutation.pushEvent(ATS_ERROR_EVENT, atsErrorEvent);
			}

		} catch (Exception exception) {
			log.error("Error while adding candidate for evaluation : ", exception);
		}

		return GreenhouseSendTestResponseBody.builder()
				.partnerInterviewId(evaluationId)
				.build();
	}

	@GetMapping(value = "/test_status", produces = "application/json")
	public ResponseEntity<GreenhouseTestStatus> testStatusHandler(
			@RequestHeader(name = "Authorization") String token,
			@RequestParam(value = "partner_interview_id", required = true) final String evaluationId) throws Exception {
		ApiKeyDAO apiKeyDAO = this.getApiKeyDAO(token);
		Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository.findById(evaluationId);
		GreenhouseTestStatus greenhouseTestStatus = null;

		log.info(String.format(
				"Greenhouse test_status API for evaluationId : %s called",
				evaluationDAO.get().getId()));

		try {
			if (evaluationDAO.isEmpty()) {
				log.error(String.format(
						"Evaluation does not exist!! evaluationId : %s",
						evaluationId));

				throw new IllegalArgumentException(String.format(
						"Evaluation does not exist!! evaluationId : %s",
						evaluationId));
			}

			greenhouseTestStatus = this.greenhouseTestStatusRequestHandler
					.getGreenhouseTestStatus(evaluationDAO.get());
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to fetch BGS score and candidate-evaluation link for EvaluationId:%s partnerId:%s",
							evaluationId,
							apiKeyDAO.getPartnerId()),
					exception);

			throw exception;
		}

		return ResponseEntity
				.ok()
				.body(greenhouseTestStatus);
	}

	private ApiKeyDAO getApiKeyDAO(String token) throws Exception {
		final Optional<ApiKeyDAO> apiKeyDAO = this.apiKeyRepository.findByKeyAndDisabledOnIsNull(token);

		if (apiKeyDAO.isEmpty()) {
			Exception exception = new InvalidAccessException("API key validation failed");
			log.error("Api Key validation failed.", exception);
			throw exception;
		}

		return apiKeyDAO.get();
	}
}
