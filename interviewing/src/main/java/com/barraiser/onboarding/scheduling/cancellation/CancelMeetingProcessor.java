/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interviewing.meeting.MeetingPlatform;
import com.barraiser.onboarding.interviewing.meeting.InterviewMeetingUtils;
import com.barraiser.onboarding.zoom.ZoomManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("cancellationZoomMeetingProcessor")
@AllArgsConstructor
public class CancelMeetingProcessor implements CancellationProcessor {

	private final ZoomManager zoomManager;
	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;

	@Override
	public void process(final CancellationProcessingData data) {
		final String meetingLink = data.getPreviousStateOfCancelledInterview().getMeetingLink();
		if (meetingLink == null || meetingLink.isEmpty()) {
			log.info("Zoom link was empty for interview {}. Something seems to have gone wrong " +
					"from process point of view. Continuing cancellation ..",
					data.getPreviousStateOfCancelledInterview());
			return;
		}

		InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		if (MeetingPlatform.ZOOM.equals(InterviewMeetingUtils.getMeetingPlatformFromURL(meetingLink))) {
			final String meetingId = this.zoomManager.getMeetingIdFromJoinUrl(meetingLink);
			this.zoomManager.cancelMeeting(meetingId);
			interviewDAO = this.interviewService.save(interviewDAO.toBuilder()
					.zoomAccountEmail(null)
					.zoomEndTime(null)
					.zoomLink(null).build());
		}
		interviewDAO = interviewDAO.toBuilder().meetingLink(null).build();
		data.setInterviewToBeCancelled(interviewDAO);
	}

}
