/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ResetInterviewService {

	public InterviewDAO resetInterview(InterviewDAO interviewDAO) {
		interviewDAO = this.resetSchedulingDetails(interviewDAO);
		interviewDAO = this.resetCancellationDetails(interviewDAO);
		interviewDAO = this.resetInterviewingDetails(interviewDAO);
		return interviewDAO;
	}

	private InterviewDAO resetSchedulingDetails(final InterviewDAO interviewDAO) {
		return interviewDAO.toBuilder()
				.startDate(null)
				.endDate(null)
				.interviewerId(null)
				.taggingAgent(null)
				.schedulingPlatform(null)
				.status(InterviewStatus.PENDING_SCHEDULING.getValue())
				.build();
	}

	private InterviewDAO resetCancellationDetails(final InterviewDAO interviewDAO) {
		return interviewDAO.toBuilder()
				.cancellationTime(null)
				.cancellationReasonId(null)
				.build();
	}

	private InterviewDAO resetInterviewingDetails(final InterviewDAO interviewDAO) {
		return interviewDAO.toBuilder()
				.zoomLink(null)
				.youtubeLink(null)
				.videoStartTime(null)
				.videoEndTime(null)
				.lastQuestionEnd(null)
				.isBadQuality(null)
				.zoomAccountEmail(null)
				.audioLink(null)
				.duplicateReason(null)
				.interviewStartTime(null)
				.actualStartDate(null)
				.actualEndDate(null)
				.submittedCodeLink(null)
				.zoomEndTime(null)
				.reopeningReasonId(null)
				.meetingLink(null)
				.build();
	}

}
