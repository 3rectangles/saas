/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.zoom.zoom_recording_event.ZoomRecordingEvent;
import com.barraiser.commons.eventing.schema.zoom.zoom_recording_stopped_event.ZoomRecordingStoppedEvent;
import com.barraiser.commons.eventing.schema.zoom.zoom_transcription_completed_event.ZoomTranscriptionCompletedEvent;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.zoom.constants.ZoomFileType;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import com.barraiser.onboarding.dal.InterviewDAO;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZoomRecordingEventHandler implements EventListener<InterviewingConsumer> {

	private static final Integer MINIMUM_RECORDING_DURATION_IN_SECONDS = 60 * 15;

	private final ObjectMapper objectMapper;
	private final InterviewUtil interviewUtil;
	private final InterviewService interviewService;
	private final String ZOOM_RECORDING_STOPPED = "ZoomRecordingStoppedEvent";
	private final String ZOOM_TRANSCRIPT_COMPLETED = "ZoomTranscriptionCompletedEvent";
	private final String ZOOM_RECORDING = "ZoomRecordingEvent";
	private ZoomRecordingStoppedEvent zoomRecordingStoppedEvent;
	private ZoomRecordingEvent zoomRecordingEvent;
	private ZoomTranscriptionCompletedEvent zoomTranscriptionCompletedEvent;
	private final InterviewStatusManager interviewStatusManager;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(ZoomRecordingStoppedEvent.class, ZoomRecordingEvent.class,
				ZoomTranscriptionCompletedEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {

		ZoomMeetingInfoDTO zoomMeetingInfoDTO = this.getZoomRecordingEventInfo(event);

		Long meetingId = zoomMeetingInfoDTO.getMeetingId();
		Long recordingStartTime = zoomMeetingInfoDTO.getRecordingStartTime();
		Long recordingEndTime = zoomMeetingInfoDTO.getRecordingEndTime();

		InterviewDAO interviewDAO = this.interviewUtil.getInterviewFromZoomMeetingId(meetingId.toString());
		if (interviewDAO == null) {
			return;
		}

		if (this.hasBotEntered(interviewDAO) || (interviewDAO.getVideoStartTime() != null &&
				this.isPreviousRecordingDurationGreaterThanCurrentDuration(interviewDAO, recordingStartTime,
						recordingEndTime, meetingId))) {
			return;
		}
		log.info("received zoom recording stopped event for interview : {}", interviewDAO.getId());

		if (!this.shouldConsiderRecording(recordingStartTime, recordingEndTime)) {
			return;
		}

		interviewDAO = this.saveInterviewRecordingTime(interviewDAO, recordingStartTime, recordingEndTime);
	}

	private ZoomMeetingInfoDTO getZoomRecordingEventInfo(Event event) {
		if (this.ZOOM_RECORDING_STOPPED.equals(event.getEventType())) {
			return this.processZoomRecordingStoppedEvent(event);
		} else if (this.ZOOM_TRANSCRIPT_COMPLETED.equals(event.getEventType())) {
			return this.processTranscriptionCompletedEvent(event);
		} else if (this.ZOOM_RECORDING.equals(event.getEventType())) {
			return this.processAllZoomRecordingProcessingCompletionEvent(event);
		}
		return null;
	}

	private ZoomMeetingInfoDTO processZoomRecordingStoppedEvent(final Event event) {
		this.zoomRecordingStoppedEvent = this.objectMapper.convertValue(event.getPayload(),
				ZoomRecordingStoppedEvent.class);

		return ZoomMeetingInfoDTO.builder()
				.meetingId(this.zoomRecordingStoppedEvent.getPayload().getObject().getId())
				.recordingStartTime(this.zoomRecordingStoppedEvent.getPayload().getObject().getRecordingFile()
						.getRecordingStart().toInstant().getEpochSecond())
				.recordingEndTime(this.zoomRecordingStoppedEvent.getPayload().getObject().getRecordingFile()
						.getRecordingEnd().toInstant().getEpochSecond())
				.build();
	}

	private ZoomMeetingInfoDTO processTranscriptionCompletedEvent(final Event event) {
		this.zoomTranscriptionCompletedEvent = this.objectMapper.convertValue(event.getPayload(),
				ZoomTranscriptionCompletedEvent.class);

		return ZoomMeetingInfoDTO.builder()
				.meetingId(this.zoomTranscriptionCompletedEvent.getPayload().getObject().getId())
				.recordingStartTime(this.zoomTranscriptionCompletedEvent.getPayload().getObject().getRecordingFiles()
						.get(0).getRecordingStart().toInstant().getEpochSecond())
				.recordingEndTime(this.zoomTranscriptionCompletedEvent.getPayload().getObject().getRecordingFiles()
						.get(0).getRecordingEnd().toInstant().getEpochSecond())
				.build();
	}

	// Captures event onces all recordings are completed and processed at zooms end
	// to be available for access.
	private ZoomMeetingInfoDTO processAllZoomRecordingProcessingCompletionEvent(final Event event) {
		this.zoomRecordingEvent = this.objectMapper.convertValue(event.getPayload(), ZoomRecordingEvent.class);
		Long meetingId = this.zoomRecordingEvent.getPayload().getObject().getId();
		final Long[] recordingStartTime = new Long[1];
		final Long[] recordingEndTime = new Long[1];
		this.zoomRecordingEvent.getPayload().getObject().getRecordingFiles().stream()
				.forEach(
						(r) -> {
							if (ZoomFileType.MP4.equals(r.getFileExtension())) {
								recordingStartTime[0] = r.getRecordingStart().toInstant().getEpochSecond();
								recordingEndTime[0] = r.getRecordingEnd().toInstant().getEpochSecond();
							}
						});
		return ZoomMeetingInfoDTO.builder()
				.meetingId(meetingId)
				.recordingStartTime(recordingStartTime[0])
				.recordingEndTime(recordingEndTime[0])
				.build();
	}

	// TODO: what to do if another recording comes greater than 15 minutes (maybe
	// yes if it is longer than current duration)
	private Boolean shouldConsiderRecording(final Long startTime, final Long endTime) {
		return endTime - startTime >= MINIMUM_RECORDING_DURATION_IN_SECONDS;
	}

	private Boolean isPreviousRecordingDurationGreaterThanCurrentDuration(final InterviewDAO interviewDAO,
			final Long recordingStartTime,
			final Long recordingEndTime,
			final Long meetingId) {
		final Long interviewStartTime = interviewDAO.getVideoStartTime();
		final Long interviewEndTime = interviewDAO.getVideoEndTime();

		if (interviewEndTime - interviewStartTime >= recordingEndTime - recordingStartTime) {
			log.info("Recording event skipped for meetingId : {}", meetingId);
			return true;
		}
		return false;
	}

	private InterviewDAO saveInterviewRecordingTime(final InterviewDAO interviewDAO,
			final Long recordingStartTime,
			final Long recordingEndTime) {
		InterviewDAO updatedInterviewDAO = interviewDAO.toBuilder()
				.videoStartTime(recordingStartTime)
				.videoEndTime(recordingEndTime)
				.build();

		updatedInterviewDAO = this.interviewService.save(updatedInterviewDAO);
		return updatedInterviewDAO;
	}

	private InterviewDAO transitionStatus(final InterviewDAO interviewDAO) {
		try {
			final InterviewStatus toStatus = this.getDestinationInterviewStatus(interviewDAO);
			if (InterviewStatus.PENDING_INTERVIEWING.getValue().equals(interviewDAO.getStatus())) {
				return this.interviewStatusManager.updateInterviewStatus(interviewDAO, toStatus, null,
						null);
			}
		} catch (final Exception e) {
			log.error(e, e);
		}
		return interviewDAO;
	}

	private InterviewStatus getDestinationInterviewStatus(final InterviewDAO interviewDAO) {

		if (this.interviewUtil.isFastrackedInterview(interviewDAO.getInterviewRound())
				|| Boolean.FALSE.equals(interviewDAO.getIsTaggingAgentNeeded())) {
			return InterviewStatus.PENDING_FEEDBACK_SUBMISSION;
		}

		return InterviewStatus.PENDING_TAGGING;
	}

	private Boolean hasBotEntered(final InterviewDAO interviewDAO) {
		return !this.interviewToStepFunctionExecutionRepository.findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(
				interviewDAO.getId(), interviewDAO.getRescheduleCount(), List.of(FlowType.INTERVIEWING_LIFECYCLE))
				.isEmpty();
	}
}
