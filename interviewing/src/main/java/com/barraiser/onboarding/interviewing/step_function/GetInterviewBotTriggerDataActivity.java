/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.step_function;

import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interviewing.meeting.MeetingPlatform;
import com.barraiser.onboarding.interviewing.meeting.InterviewMeetingUtils;
import com.barraiser.onboarding.interviewing.step_function.dto.InterviewingLifecycleDTO;
import com.barraiser.onboarding.zoom.ZoomManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
@Log4j2
public class GetInterviewBotTriggerDataActivity implements InterviewingLifecycleSfnActivity {
	private final static String ZOOM_JOIN_URL = "https://us05web.zoom.us/wc/join/%s";
	private final static String MEETING_PASSCODE = "123456";

	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;
	private final ZoomManager zoomManager;
	private final ConfigComposer configComposer;

	@Override
	public String name() {
		return "interviewing-get-bot-data";
	}

	@Override
	public InterviewingLifecycleDTO process(String input) throws Exception {
		final InterviewingLifecycleDTO data = this.objectMapper.readValue(input, InterviewingLifecycleDTO.class);
		final Optional<InterviewDAO> interviewDAO = this.interViewRepository.findById(data.getInterviewId());

		if (interviewDAO.isPresent()) {
			final Boolean isSaasInterview = this.interviewUtil.isSaasInterview(interviewDAO.get().getInterviewRound());
			final String botJoiningLink = this.getBotJoiningLink(interviewDAO.get().getMeetingLink());
			data.setTriggerBotData(InterviewingLifecycleDTO.TriggerBotData.builder()
					.duration(Long.toString(interviewDAO.get().getEndDate() - interviewDAO.get().getStartDate()))
					.meetingURL(botJoiningLink)
					.meetingPasscode(this.getMeetingPasscode(interviewDAO.get().getMeetingLink()))
					.zoomMeetingUrl(botJoiningLink)
					.isRecordingConsentNeeded(this.getRecordingConsentNeeded(isSaasInterview))
					.zoomPasscode(this.getMeetingPasscode(interviewDAO.get().getMeetingLink()))
					.interviewScheduledTime(interviewDAO.get().getStartDate() * 1000)
					.partnerId(interviewDAO.get().getPartnerId())
					.build());
		} else {
			throw new IllegalArgumentException("no interview found for interview_id : " + data.getInterviewId());
		}

		return data;
	}

	private String getMeetingPasscode(final String meetingLink) {
		return MeetingPlatform.ZOOM
				.equals(InterviewMeetingUtils.getMeetingPlatformFromURL(meetingLink))
						? this.getZoomPasscode(meetingLink)
						: MEETING_PASSCODE;
	}

	private String getBotJoiningLink(final String meetingLink) {
		return MeetingPlatform.ZOOM
				.equals(InterviewMeetingUtils.getMeetingPlatformFromURL(meetingLink))
						? String.format(ZOOM_JOIN_URL,
								this.zoomManager.getMeetingIdFromJoinUrl(meetingLink))
						: meetingLink;
	}

	private Boolean getRecordingConsentNeeded(final Boolean isSaasInterview) {
		List<String> tags = new ArrayList<>();
		if (isSaasInterview)
			tags.add("interview_type.internal");
		try {
			JsonNode config = this.configComposer.compose("interview_bot_config", tags);
			return config != null && config.get("is_recording_consent_needed").asBoolean();
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	private String getZoomPasscode(final String zoomMeetingLink) {
		return zoomMeetingLink.contains("pwd=") ? zoomMeetingLink.split("pwd=")[1] : MEETING_PASSCODE;

	}
}
