/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.zoom;

import com.barraiser.common.files.FileDownloadService;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.zoom.zoom_transcription_completed_event.ZoomTranscriptionCompletedEvent;
import com.barraiser.media_management.transcript.Transcript;
import com.barraiser.media_management.transcript.TranscriptUploader;
import com.barraiser.media_management.transcript.TranscriptsEventConsumer;
import com.barraiser.media_management.transcript.VTTTranscriptParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ZoomTranscriptionCompletedEventHandler implements EventListener<TranscriptsEventConsumer> {
	private final ObjectMapper objectMapper;
	private final MediaClient mediaClient;
	private final FileDownloadService fileDownloadService;
	private final VTTTranscriptParser transcriptParser;
	private final TranscriptUploader transcriptUploader;

	@Override
	public List<Class> eventsToListen() {
		return List.of(ZoomTranscriptionCompletedEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final ZoomTranscriptionCompletedEvent zoomTranscriptionCompletedEvent = this.objectMapper
				.convertValue(event.getPayload(), ZoomTranscriptionCompletedEvent.class);

		final Long meetingId = zoomTranscriptionCompletedEvent.getPayload().getObject().getId();
		log.info("zoom transcript received for meeting id : " + meetingId);

		final String interviewId = this.mediaClient.getEntityForZoomId(meetingId.toString());
		if (interviewId == null) {
			return;
		}

		log.info("zoom transcript received for interview : " + interviewId);

		final String transcriptDownloadLink = zoomTranscriptionCompletedEvent.getPayload().getObject()
				.getRecordingFiles().get(0).getDownloadUrl();
		final String zoomTranscript = this.fileDownloadService.downloadFileAsStringInMemory(transcriptDownloadLink);
		final Transcript transcript = this.transcriptParser.parse(zoomTranscript);
		this.transcriptUploader.uploadTranscriptForInterview(transcript, interviewId);
	}
}
