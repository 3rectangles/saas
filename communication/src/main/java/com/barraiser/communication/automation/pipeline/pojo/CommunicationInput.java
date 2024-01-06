/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline.pojo;

import com.barraiser.common.entity.Entity;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class CommunicationInput {
	private final Entity entity;

	private final String eventType;

	private final Object eventPayload;

	private final String templateId;

	private final RecipientType recipientType;

	private final Channel channel;
}
