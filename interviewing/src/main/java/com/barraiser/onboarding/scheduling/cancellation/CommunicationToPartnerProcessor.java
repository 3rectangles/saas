/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@AllArgsConstructor
@Component
public class CommunicationToPartnerProcessor implements CancellationProcessor {

	private final JiraWorkflowManager jiraWorkflowManager;
	private final DateUtils dateUtils;
	private final CandidateInformationManager candidateInformationManager;
	private final CancellationReasonRepository cancellationReasonRepository;
	private static final String CANCELLATION_ID_FOR_CANDIDATE_DID_NOT_CONFIRM = "80";

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonRepository
				.findById(data.getPreviousStateOfCancelledInterview().getCancellationReasonId()).get();

		if (this.shouldCommunicateToPartner(cancellationReasonDAO)) {
			final String comment = this.getCommentForPartner(data.getPreviousStateOfCancelledInterview(),
					cancellationReasonDAO);
			this.jiraWorkflowManager.addCommentInJira(data.getPreviousStateOfCancelledInterview().getEvaluationId(),
					JiraCommentDTO.builder().body(comment).build());
		}
	}

	private Boolean shouldCommunicateToPartner(final CancellationReasonDAO cancellationReasonDAO) {
		return Objects.equals(cancellationReasonDAO.getCancellationType(), "CANDIDATE") || Objects.equals(
				cancellationReasonDAO.getId(), CANCELLATION_ID_FOR_CANDIDATE_DID_NOT_CONFIRM)
				|| Objects.equals(cancellationReasonDAO.getCancellationType(), "CANDIDATE_EXPERT");
	}

	private String getCommentForPartner(final InterviewDAO interviewDAO,
			final CancellationReasonDAO cancellationReasonDAO) {
		return String.format(
				"Interview scheduled at %s for %s is cancelled because of the following reason at %s."
						+ System.lineSeparator() +
						"\nReason: %s",
				this.dateUtils.getFormattedDateString(interviewDAO.getStartDate(),
						interviewDAO.getIntervieweeTimezone(), DateUtils.TIME_IN_12_HOUR_FORMAT),
				this.getIntervieweeName(interviewDAO.getIntervieweeId()),
				this.dateUtils.getFormattedDateString(Long.parseLong(interviewDAO.getCancellationTime()),
						interviewDAO.getIntervieweeTimezone(), DateUtils.TIME_IN_12_HOUR_FORMAT),
				cancellationReasonDAO.getCustomerDisplayableReason());
	}

	private String getIntervieweeName(final String intervieweeId) {
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(intervieweeId);
		return (candidate.getFirstName() == null ? "" : candidate.getFirstName()) + " "
				+ (candidate.getLastName() == null ? "" : candidate.getLastName());
	}

}
