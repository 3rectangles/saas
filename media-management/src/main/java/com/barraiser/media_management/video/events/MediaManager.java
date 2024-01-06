/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.video.events;

import com.barraiser.media_management.dal.MediaDAO;
import com.barraiser.media_management.repository.MediaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@AllArgsConstructor
@Log4j2
public class MediaManager {
	private final MediaRepository mediaRepository;

	public MediaDAO save(final MediaDAO mediaDAO) {
		return this.mediaRepository.save(mediaDAO);
	}

	@Transactional
	public void deleteAllByEntityId(final String entityId) {
		this.mediaRepository.deleteAllByEntityId(entityId);
	}

	@Transactional
	public void deleteAllByEntityIdAndFormat(final String entityId, final String format) {

	}
}
