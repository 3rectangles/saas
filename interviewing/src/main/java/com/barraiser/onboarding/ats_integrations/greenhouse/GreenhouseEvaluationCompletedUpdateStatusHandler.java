/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.SuppressFailure;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.config.GreenhouseConfig;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.GreenhouseDAO;
import com.barraiser.onboarding.dal.GreenhouseRepository;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@SuppressFailure
@Component
@AllArgsConstructor
public class GreenhouseEvaluationCompletedUpdateStatusHandler
		implements EventListener<InterviewingConsumer> {
	private static final String PATCH_REQUEST = "PATCH";
	private static final String AUTHORIZATION = "Authorization";

	private final ObjectMapper objectMapper;
	private final EvaluationRepository evaluationRepository;
	private final GreenhouseRepository greenhouseRepository;
	private final ErrorCommunication errorCommunication;
	private final GreenhouseConfig greenhouseConfig;

	@Override
	public List<Class> eventsToListen() {
		return List.of(EvaluationCompleted.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		log.info("Greenhouse status update API caller after receiving EvaluationCompleted event");
		final EvaluationCompleted evaluationCompleted = this.objectMapper.convertValue(event.getPayload(),
				EvaluationCompleted.class);

		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(evaluationCompleted.getEvaluation().getId())
				.get();

		final Optional<GreenhouseDAO> greenhouseDAO = this.greenhouseRepository
				.findByEvaluationId(evaluationDAO.getId());

		if (greenhouseDAO.isPresent()) {
			this.markTestAsCompletedOnGreenhouse(greenhouseDAO.get());
		}
	}

	private void markTestAsCompletedOnGreenhouse(final GreenhouseDAO greenhouseDAO)
			throws Exception {
		log.info(
				String.format(
						"Sending patch request for test status update to greenhouse for"
								+ " evaluationId : %s",
						greenhouseDAO.getEvaluationId()));

		final String authorizationHeader = this.greenhouseConfig.getAuthorizationHeaderForAPICall();

		try {
			final URIBuilder uriBuilder = new URIBuilder(greenhouseDAO.getUpdateStatusUrl());
			final HttpPatch patch = new HttpPatch(uriBuilder.build());
			patch.addHeader(AUTHORIZATION, authorizationHeader);

			final HttpClient client = HttpClientBuilder.create().build();

			final HttpResponse response = client.execute(patch);

			log.info(
					String.format(
							"Greenhouse status update for evaluationId: %s patch request response"
									+ " code : %s",
							greenhouseDAO.getEvaluationId(),
							response.getStatusLine().getStatusCode()));
		} catch (final Exception e) {
			this.errorCommunication.sendFailureEmailToTech("Greenhouse send test failed: ", e);
			log.info(e);
		}
	}
}
