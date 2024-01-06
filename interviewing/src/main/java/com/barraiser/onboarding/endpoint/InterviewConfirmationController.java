/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewHistoryManager;
import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationManager;
import com.barraiser.onboarding.scheduling.lifecycle.DTO.InterviewConfirmationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.barraiser.onboarding.common.Constants.CANCELLED_VIA_IVR_REASON_ID;

@Log4j2
@RestController
@AllArgsConstructor
public class InterviewConfirmationController {
	public static final String JIRA_INTERVIEW_CANCELLED_KEY = "jira_interview_round_cancelled_status";

	final InterviewConfirmationRepository interviewConfirmationRepository;
	final InterViewRepository interViewRepository;
	final InterviewCancellationCommunicationService interviewCancellationCommunicationService;
	private final InterviewHistoryManager interviewHistoryManager;

	private final InterviewCancellationManager interviewCancellationManager;

	@PutMapping(value = "/candidate-interview-confirmation/{interviewId}")
	public void updateCandidateInterviewConfirmationStatus(
			@PathVariable("interviewId") final String interviewId,
			@RequestBody final InterviewConfirmationWebhookRequestBody requestBody)
			throws Exception {
		log.info(
				"The variables in the request are : {},{},{},{},{}",
				interviewId,
				requestBody.rsvp,
				requestBody.channel);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();

		this.validateInterviewForConfirmation(interviewDAO);

		final InterviewConfirmationDAO interviewConfirmationDAO = this.getOrCreateInterviewConfirmation(interviewDAO,
				requestBody.channel);
		this.interviewConfirmationRepository.save(
				interviewConfirmationDAO.toBuilder()
						.interviewId(interviewId)
						.candidateConfirmation(requestBody.rsvp)
						.candidateConfirmationTime(Instant.now())
						.communicationChannel(requestBody.channel)
						.rescheduleCount(interviewDAO.getRescheduleCount())
						.build());

		if (!requestBody.rsvp) {
			final InterviewDAO interviewToBeCancelled = interviewDAO.toBuilder()
					.cancellationTime(String.valueOf(Instant.now().getEpochSecond()))
					.cancellationReasonId(CANCELLED_VIA_IVR_REASON_ID)
					.build();

			this.interviewCancellationManager.processInterviewCancelledInConfirmationFlow(
					interviewToBeCancelled,
					interviewToBeCancelled.getIntervieweeId(),
					requestBody.getSource());
		}
	}

	@GetMapping(value = "/candidate-interview-confirmation/{interviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public InterviewConfirmationStatusResponseBody getCandidateInterviewConfirmationStatus(
			@PathVariable("interviewId") final String interviewId,
			final HttpServletResponse response,
			@RequestParam("rescheduleCount") final Integer rescheduleCount)
			throws IOException {

		InterviewDAO savedInterviewDAO = this.interViewRepository
				.findById(interviewId)
				.orElseThrow(
						() -> new IllegalArgumentException("Interview doesn't exists"));
		final Integer actualRescheduleCount = rescheduleCount != null ? rescheduleCount
				: savedInterviewDAO.getRescheduleCount();
		;

		final Optional<InterviewConfirmationDAO> interviewConfirmationDAOOptional = this.interviewConfirmationRepository
				.findTopByInterviewIdAndCandidateConfirmationTimeNotNullAndRescheduleCountOrderByCandidateConfirmationTimeDesc(
						interviewId, actualRescheduleCount);

		String interviewConfirmationStatus = interviewConfirmationDAOOptional
				.map(
						interviewConfirmationDAO -> interviewConfirmationDAO.getCandidateConfirmation()
								? InterviewConfirmationStatus.CONFIRMED
								: InterviewConfirmationStatus.DENIED)
				.orElse(InterviewConfirmationStatus.NOACTION)
				.toString();

		final InterviewHistoryDAO interviewHistoryDAO = this.interviewHistoryManager
				.getLatestByFieldValueAndReschedulingCount(
						savedInterviewDAO.getId(),
						InterviewStatus.CANCELLATION_DONE.getValue(),
						actualRescheduleCount);

		final boolean isInterviewCancelled = interviewHistoryDAO != null;

		if (isInterviewCancelled) {
			interviewConfirmationStatus = "CANCELLED";
		}

		return InterviewConfirmationStatusResponseBody.builder()
				.candidateStatus(interviewConfirmationStatus.toString())
				.build();
	}

	private InterviewConfirmationDAO getOrCreateInterviewConfirmation(
			final InterviewDAO interviewDAO, final String channel) {
		return this.interviewConfirmationRepository
				.findByInterviewIdAndCommunicationChannelAndRescheduleCount(
						interviewDAO.getId(), channel, interviewDAO.getRescheduleCount())
				.orElse(
						InterviewConfirmationDAO.builder()
								.id(UUID.randomUUID().toString())
								.build());
	}

	private void validateInterviewForConfirmation(final InterviewDAO interviewDAO) {
		if (!List.of(InterviewStatus.PENDING_TA_ASSIGNMENT.getValue(), InterviewStatus.PENDING_INTERVIEWING.getValue())
				.contains(interviewDAO.getStatus())) {
			throw new IllegalArgumentException();
		}
	}
}
