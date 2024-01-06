/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.video.events;

import com.barraiser.commons.eventing.EventConsumer;
import com.barraiser.media_management.common.MediaManagementStaticAppConfigValues;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class VideoUploadCompletionConsumer extends EventConsumer {
	private final MediaManagementStaticAppConfigValues staticAppConfigValues;

	@Override
	public String queueUrl() {
		return this.staticAppConfigValues.getVideoEventsQueue();
	}
}
