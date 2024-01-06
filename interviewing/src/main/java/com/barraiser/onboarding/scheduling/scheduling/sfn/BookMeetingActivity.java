/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.featureToggle.InterviewLevelFeatureToggleManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.webex.WebexMeetingManager;
import com.barraiser.onboarding.webex.dto.CreateWebexMeetingResponseDTO;
import com.barraiser.onboarding.zoom.ZoomManager;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("schedulingZoomMeetingProcessor")
@AllArgsConstructor
public class BookMeetingActivity implements InterviewSchedulingActivity {
	public static final String BOOK_MEETING = "book-meeting";
	private final static String ZOOM_MEETING_PATH = "https://us02web.zoom.us/j/";
	private final InterViewRepository interViewRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final WebexMeetingRepository webexMeetingRepository;
	private final ZoomManager zoomManager;
	private final WebexMeetingManager webexMeetingManager;
	private final ErrorCommunication errorCommunication;
	private final InterviewService interviewService;
	private final InterviewLevelFeatureToggleManager featureToggleManager;
	private final ObjectMapper objectMapper;

	public String constructZoomLinkWithPassword(final ZoomMeetingDTO meeting) {
		String zoomLink = ZOOM_MEETING_PATH + meeting.getMeetingId();

		if (meeting.getEncryptedPassword() != null && !meeting.getEncryptedPassword().isEmpty()) {
			zoomLink = ZOOM_MEETING_PATH + meeting.getMeetingId() + "?pwd=" + meeting.getEncryptedPassword();
		}
		return zoomLink;
	}

	private void constructDataToCommunicateError() throws Exception {
		final String subject = "No Available Slots Found To Schedule Zoom Meeting";
		final String errorMessage = "No available slots found to schedule zoom meeting. Try booking another slot";
		this.errorCommunication.informErrorToOps(subject, errorMessage);
	}

	private void scheduleWebexMeeting(final InterviewDAO interview, final String topic) {
		final CreateWebexMeetingResponseDTO meeting = this.webexMeetingManager.scheduleMeeting(interview.getStartDate(),
				interview.getEndDate() + 30 * 60, topic);

		this.webexMeetingRepository.save(WebexMeetingDAO.builder()
				.id(meeting.getId())
				.interviewId(interview.getId())
				.rescheduleCount(interview.getRescheduleCount())
				.meetingNumber(meeting.getMeetingNumber())
				.joinLink(meeting.getWebLink())
				.startDate(interview.getStartDate())
				.endDate(interview.getEndDate() + 30 * 60)
				.password(meeting.getPassword())
				.build());
	}

	private Boolean shouldScheduleWebexMeeting(final InterviewDAO interview) {
		final Map<String, Boolean> featureToggles = this.featureToggleManager
				.getFeatureToggles(interview.getId());

		return featureToggles.getOrDefault("webex", false);
	}

	@Override
	public String name() {
		return BOOK_MEETING;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		// Zoom meeting must be scheduled for 30 minutes extra
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		final InterviewDAO interview = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		if (interview.getMeetingLink() != null) {
			return data;
		}
		final Long zoomMeetingEndTime = interview.getEndDate() + 30 * 60;

		List<InterviewDAO> interviewsClashing = new ArrayList<>(this.interViewRepository
				.findAllByStartDateLessThanAndZoomEndTimeGreaterThan(
						zoomMeetingEndTime, interview.getStartDate()));

		// remove duplicate interviews from interviewsClashing by comparing their ids
		// and
		// remove interviews which does not have zoom account set
		interviewsClashing = interviewsClashing.stream()
				.<Map<String, InterviewDAO>>collect(HashMap::new, (m, i) -> m.put(i.getId(), i), Map::putAll)
				.values()
				.stream()
				.filter(i -> i.getZoomAccountEmail() != null)
				.collect(Collectors.toList());

		final List<String> zoomAccountsUsages = interviewsClashing.stream().map(InterviewDAO::getZoomAccountEmail)
				.collect(Collectors.toList());
		String zoomAccountToBeUsed;
		try {
			zoomAccountToBeUsed = this.zoomManager.getZoomAccountToUse(zoomAccountsUsages);
		} catch (final IllegalArgumentException e) {
			this.constructDataToCommunicateError();
			throw new IllegalArgumentException(e.getMessage());
		}

		final CandidateDAO interviewee = this.candidateInformationManager.getCandidate(interview.getIntervieweeId());

		final String zoomTopic = "Interview "
				+ (interview.getInterviewRound().length() > 0 ? interview.getInterviewRound().charAt(0) : "")
				+ " - " + interviewee.getFirstName() + " " + interviewee.getLastName();

		final ZoomMeetingDTO meeting = this.zoomManager.scheduleMeeting(zoomAccountToBeUsed, interview.getStartDate(),
				zoomMeetingEndTime, zoomTopic);
		data.setZoomMeeting(meeting);

		if (this.shouldScheduleWebexMeeting(interview)) {
			this.scheduleWebexMeeting(interview, zoomTopic);
		}

		// Sometimes the join url does not have password suffixed to it.
		final String zoomLink = this.constructZoomLinkWithPassword(meeting);
		this.interviewService.save(interview.toBuilder().zoomLink(zoomLink)
				.zoomEndTime(interview.getStartDate() + meeting.getDuration() * 60)
				.zoomAccountEmail(meeting.getHostEmail())
				.meetingLink(zoomLink)
				.build());
		return data;
	}
}
