/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.service;

import static com.barraiser.media_management.common.Constants.*;

import com.barraiser.common.model.Media;
import com.barraiser.media_management.common.MediaManagementStaticAppConfigValues;
import com.barraiser.media_management.dal.MediaDAO;
import com.barraiser.media_management.repository.MediaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class MediaManagementService {

	private final MediaManagementStaticAppConfigValues staticAppConfigValues;
	private final MediaRepository mediaRepository;
	private final ObjectMapper objectMapper;

	public List<Media> getMedia(final String entityType, final String entityId) {
		return this.mediaRepository.findAllByEntityTypeAndEntityIdOrderByCreatedOnDesc(entityType, entityId).stream()
				.map(
						x -> {
							return this.objectMapper.convertValue(x, Media.class).toBuilder()
									.uri(this.constructMediaUri(x))
									.build();
						})
				.collect(Collectors.toList());
	}

	private String constructMediaUri(final MediaDAO media) {
		switch (media.getInternalType()) {
			case MEDIA_INTERNAL_TYPE_HLS:
				return this.getTranscodedVideoURL(media);

			default:
				return this.getMediaURL(media);
		}
	}

	private String getMediaURL(final MediaDAO media) {
		if (MEDIA_INTERNAL_TYPE_SOURCE.equalsIgnoreCase(media.getInternalType())) {
			return "https://"
					+ this.staticAppConfigValues.getMediaServingDomain()
					+ "/"
					+ media.getContext().toLowerCase()
					+ "/"
					+ media.getEntityId()
					+ "/"
					+ media.getId()
					+ media.getFormat();
		}

		return null;
	}

	private String getTranscodedVideoURL(final MediaDAO media) {
		return "https://"
				+ this.staticAppConfigValues.getMediaServingDomain()
				+ "/"
				+ media.getContext().toLowerCase()
				+ "/"
				+ media.getEntityId()
				+ "/"
				+ media.getInternalType()
				+ "/"
				+ media.getId()
				+ TRANSCODED_VIDEO_FILE_SUFFIX;
	}
}
