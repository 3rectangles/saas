/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.video.events;

import static com.barraiser.media_management.common.Constants.*;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.aws_event.S3eventViaCloudtrail.AWSAPICallViaCloudTrail;
import com.barraiser.media_management.dal.MediaDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class VideoUploadCompletionEventHandler
		implements EventListener<VideoUploadCompletionConsumer> {

	private final ObjectMapper objectMapper;
	private final MediaManager mediaManager;

	private static final String TRANSCODED_VIDEOS_BUCKET_KEY = "barraiser-videos-transcoded";
	private static final String INTERVIEW_RECORDING_CONTEXT = "interview_recording";

	@Override
	public List<Class> eventsToListen() {
		return null;
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final AWSAPICallViaCloudTrail videoUploadEvent = this.objectMapper.convertValue(event.getPayload(),
				AWSAPICallViaCloudTrail.class);
		this.updateMediaDetails(videoUploadEvent);
	}

	private void updateMediaDetails(final AWSAPICallViaCloudTrail videoUploadEvent) {

		if (!this.isFolderCreationEvent(videoUploadEvent)
				&& !this.isTranscodedVideoUploadEvent(videoUploadEvent)
				&& this.isEventToBeProcessed(videoUploadEvent)) {
			if (this.isInterviewRecording(videoUploadEvent)) {
				this.processSourceVideo(videoUploadEvent);
			} else {
				this.processLiveRecording(videoUploadEvent);
			}
		}
	}

	@Transactional
	private void processSourceVideo(final AWSAPICallViaCloudTrail videoUploadEvent) {
		final String[] filePath = videoUploadEvent.getRequestParameters().getKey().split("/");
		final String mediaId = filePath[2].split("\\.")[0];
		final String mediaFormat = this.getFileFormat(filePath[2]);
		final String context = filePath[0].toUpperCase();
		final String relatedEntityId = filePath[1];

		final MediaDAO mediaDAO = MediaDAO.builder()
				.id(mediaId)
				.category(MEDIA_CATEGORY_VIDEO)
				.entityType(ENTTITY_TYPE_INTERVIEW)
				.entityId(relatedEntityId)
				.format(mediaFormat)
				.internalType(MEDIA_INTERNAL_TYPE_SOURCE)
				.context(context)
				.build();

		this.mediaManager.deleteAllByEntityIdAndFormat(relatedEntityId, mediaFormat);

		this.mediaManager.save(mediaDAO);
	}

	@Transactional
	private void processLiveRecording(final AWSAPICallViaCloudTrail videoUploadEvent) {
		final String[] filePath = videoUploadEvent.getRequestParameters().getKey().split("/");
		final String mediaId = filePath[2].split("\\.")[0];
		final String context = filePath[0].toUpperCase();
		final String relatedEntityId = filePath[1];

		final MediaDAO mediaDAO = MediaDAO.builder()
				.id(mediaId)
				.category(MEDIA_CATEGORY_VIDEO)
				.entityType(ENTTITY_TYPE_INTERVIEW)
				.entityId(relatedEntityId)
				.format(TRANSCODED_VIDEO_FILE_FORMAT)
				.internalType(MEDIA_INTERNAL_TYPE_SOURCE)
				.context(context)
				.build();

		this.mediaManager.deleteAllByEntityIdAndFormat(relatedEntityId, TRANSCODED_VIDEO_FILE_FORMAT);

		this.mediaManager.save(mediaDAO);
	}

	private Boolean isEventToBeProcessed(final AWSAPICallViaCloudTrail videoUploadEvent) {
		return videoUploadEvent
				.getRequestParameters()
				.getKey()
				.endsWith(TRANSCODED_VIDEO_FILE_FORMAT);
	}

	private Boolean isTranscodedVideoUploadEvent(final AWSAPICallViaCloudTrail videoUploadEvent) {
		return TRANSCODED_VIDEOS_BUCKET_KEY.equalsIgnoreCase(
				videoUploadEvent.getRequestParameters().getBucketName());
	}

	private Boolean isFolderCreationEvent(final AWSAPICallViaCloudTrail videoUploadEvent) {
		final String createdDataKey = videoUploadEvent.getRequestParameters().getKey();
		return createdDataKey.endsWith("/");
	}

	private Boolean isInterviewRecording(final AWSAPICallViaCloudTrail videoUploadEvent) {
		return videoUploadEvent.getRequestParameters().getKey().contains(INTERVIEW_RECORDING_CONTEXT);
	}

	private String getFileFormat(final String filename) {
		return filename.substring(filename.lastIndexOf("."));
	}
}
