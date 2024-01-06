/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.drop_candidature;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.interview.InterviewCompletionService;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationManager;
import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationOnJiraManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static com.barraiser.onboarding.common.Constants.ROUND_NOT_REQUIRED_REASON_ID;

@Log4j2
@Component
@AllArgsConstructor
public class DropCandidatureInterviewManager {

	private final DateUtils dateUtils;
	private final InterviewCancellationOnJiraManager interviewCancellationOnJiraManager;
	private final InterviewService interviewService;
	private final InterviewCancellationManager interviewCancellationManager;

	public final static String INTERVIEW_UPDATION_SOURCE = "DROP_CANDIDATURE";

	public static final String DROP_CANDIDATURE_ACTION_INVALID_ERROR = "Drop candidature is not allowed at this stage as interviews are in process. Kindly contact BarRaiser Team.";
	public static final Integer DROP_CANDIDATURE_ACTION_INVALID_ERROR_CODE = 1002;

	public void updateInterviews(final AuthenticatedUser user, final List<InterviewDAO> interviews) throws Exception {

		for (InterviewDAO interview : interviews) {
			if (InterviewStatus.fromString(interview.getStatus()).isInProcess()) {
				if (InterviewStatus.PENDING_INTERVIEWING.equals(InterviewStatus.fromString(interview.getStatus()))) {
					this.handlePendingInterviewing(user, interview);
				} else if (!InterviewStatus.fromString(interview.getStatus()).isConductionPending()) {
					throw new IllegalOperationException(DROP_CANDIDATURE_ACTION_INVALID_ERROR,
							DROP_CANDIDATURE_ACTION_INVALID_ERROR, DROP_CANDIDATURE_ACTION_INVALID_ERROR_CODE);
				} else {
					this.processForCancellation(user, interview);
				}
			}
		}
	}

	private void handlePendingInterviewing(final AuthenticatedUser user, final InterviewDAO interview)
			throws Exception {
		if (this.dateUtils.isTimeMoreThanNminutesFromScheduledTime(Instant.now().getEpochSecond(), 5,
				interview.getStartDate())) {
			log.info(
					"Drop candidature request for interview {} by user {}. Processing for cancellation.",
					interview.getId(), user.getUserName());
			this.processForCancellation(user, interview);
		} else {
			log.info(
					"Candidature dropping request for interview {} made inside of 5 mins from interview start. We do not support this at the moment.",
					interview.getId());
			throw new IllegalOperationException(DROP_CANDIDATURE_ACTION_INVALID_ERROR,
					DROP_CANDIDATURE_ACTION_INVALID_ERROR, DROP_CANDIDATURE_ACTION_INVALID_ERROR_CODE);
		}
	}

	private void processForCancellation(final AuthenticatedUser user, final InterviewDAO interview) throws Exception {

		final Long interviewCancellationTime = Instant.now().getEpochSecond();
		final String interviewCancellationReason = ROUND_NOT_REQUIRED_REASON_ID;

		final InterviewDAO interviewWithCancellationDetails = interview.toBuilder()
				.cancellationTime(interviewCancellationTime.toString())
				.cancellationReasonId(interviewCancellationReason)
				.isPendingScheduling(Boolean.FALSE)
				.status(InterviewStatus.CANCELLATION_DONE.getValue())
				.build();

		if (InterviewStatus.fromString(interview.getStatus()).isScheduled()) {
			this.interviewCancellationManager.cancel(interviewWithCancellationDetails, user.getUserName(),
					INTERVIEW_UPDATION_SOURCE);
		} else {
			this.markInterviewAsCancelled(interviewWithCancellationDetails);
		}
	}

	private void markInterviewAsCancelled(final InterviewDAO interviewWithCancellationDetails) {
		this.interviewService.save(interviewWithCancellationDetails);
		this.interviewCancellationOnJiraManager.cancelInterviewOnJira(interviewWithCancellationDetails.getId(),
				interviewWithCancellationDetails.getCancellationReasonId(),
				Long.valueOf(interviewWithCancellationDetails.getCancellationTime()));
	}

}
