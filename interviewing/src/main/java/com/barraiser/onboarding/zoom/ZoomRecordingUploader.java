/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.zoom.zoom_recording_completed_event.ZoomRecordingCompletedEvent;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.zoom.constants.ZoomFileType;
import com.barraiser.onboarding.zoom.dto.UploadInterviewRecordingDTO;
import com.barraiser.onboarding.zoom.dto.ZoomRecordingsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class ZoomRecordingUploader implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final ZoomManager zoomManager;
	private final InterviewUtil interviewUtil;
	private final InterviewRecordingUploaderClient client;

	@Override
	public List<Class> eventsToListen() {
		return List.of(ZoomRecordingCompletedEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final ZoomRecordingCompletedEvent recordingEvent = this.objectMapper.convertValue(event.getPayload(),
				ZoomRecordingCompletedEvent.class);

		final String meetingId = recordingEvent.getPayload().getObject().getId().toString();

		final ZoomRecordingsDTO.RecordingFile recording = this.getLargestVideoRecording(meetingId);

		if (recording == null) {
			log.info("no recording found for meeting : " + meetingId);
			return;
		}

		final InterviewDAO interview = this.interviewUtil.getInterviewFromZoomMeetingId(meetingId);

		if (interview == null) {
			log.info("interview not found for zoom meeting : " + meetingId);
			return;
		}

		this.client.uploadRecording(UploadInterviewRecordingDTO.builder()
				.interviewId(interview.getId())
				.downloadUrl(recording.getDownloadUrl())
				.videoId(UUID.randomUUID().toString())
				.build());
	}

	private ZoomRecordingsDTO.RecordingFile getLargestVideoRecording(final String meetingId) {
		ZoomRecordingsDTO.RecordingFile videoRecording = null;
		final List<String> meetingInstances = this.zoomManager.getMeetingInstances(meetingId);

		Integer duration = 0;
		for (final String meetingInstance : meetingInstances) {
			final String doubleEncodedMeetingUUID = URLEncoder
					.encode(URLEncoder.encode(meetingInstance, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
			final ZoomRecordingsDTO recordingsDTO = this.zoomManager.getMeetingRecordings(doubleEncodedMeetingUUID);
			if (recordingsDTO == null)
				continue;
			if (recordingsDTO.getDuration() > duration) {
				duration = recordingsDTO.getDuration();
				videoRecording = this.getVideoRecordingFile(recordingsDTO);
			}
		}
		return videoRecording;
	}

	private ZoomRecordingsDTO.RecordingFile getVideoRecordingFile(final ZoomRecordingsDTO recordingsDTO) {
		for (final ZoomRecordingsDTO.RecordingFile recording : recordingsDTO.getRecordingFiles()) {
			if (ZoomFileType.MP4.equals(recording.getFileType())) {
				return recording;
			}
		}
		return null;
	}
}
