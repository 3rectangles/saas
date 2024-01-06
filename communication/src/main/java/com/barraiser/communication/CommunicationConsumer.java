/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication;

import com.barraiser.commons.eventing.EventConsumer;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CommunicationConsumer extends EventConsumer {

	private final CommunicationStaticAppConfig staticAppConfig;

	@Override
	public String queueUrl() {
		return this.staticAppConfig.getSlackEventSQSUrl();
	}
}
