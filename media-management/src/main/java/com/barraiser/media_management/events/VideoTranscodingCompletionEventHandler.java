/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.events;

import static com.barraiser.media_management.common.Constants.ENTTITY_TYPE_INTERVIEW;
import static com.barraiser.media_management.common.Constants.MEDIA_CATEGORY_VIDEO;
import static com.barraiser.media_management.common.Constants.MEDIA_INTERNAL_TYPE_HLS;
import static com.barraiser.media_management.common.Constants.TRANSCODED_VIDEO_FILE_FORMAT;
import static com.barraiser.media_management.common.Constants.TRANSCODED_VIDEO_FILE_SUFFIX;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.aws_event.S3eventViaCloudtrail.AWSAPICallViaCloudTrail;
import com.barraiser.media_management.dal.MediaDAO;
import com.barraiser.media_management.repository.MediaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class VideoTranscodingCompletionEventHandler
		implements EventListener<VideoTranscodingCompletionEventConsumer> {

	private final ObjectMapper objectMapper;
	private final MediaRepository mediaRepository;

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

		if (this.isEventToBeProcessed(videoUploadEvent)) {
			final String[] filePath = videoUploadEvent.getRequestParameters().getKey().split("/");
			final String mediaId = filePath[3].split(TRANSCODED_VIDEO_FILE_SUFFIX)[0];
			final String context = filePath[0].toUpperCase();
			final String entityId = filePath[1];

			final MediaDAO mediaDAO = MediaDAO.builder()
					.id(mediaId)
					.category(MEDIA_CATEGORY_VIDEO)
					.entityType(ENTTITY_TYPE_INTERVIEW)
					.entityId(entityId)
					.format(TRANSCODED_VIDEO_FILE_FORMAT)
					.internalType(MEDIA_INTERNAL_TYPE_HLS)
					.context(context)
					.build();

			this.mediaRepository.save(mediaDAO);
		}
	}

	private Boolean isEventToBeProcessed(final AWSAPICallViaCloudTrail videoUploadEvent) {
		return videoUploadEvent
				.getRequestParameters()
				.getKey()
				.endsWith(TRANSCODED_VIDEO_FILE_SUFFIX);
	}
}
