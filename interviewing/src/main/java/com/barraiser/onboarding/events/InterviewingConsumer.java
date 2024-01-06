/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events;

import com.barraiser.commons.eventing.EventConsumer;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class InterviewingConsumer extends EventConsumer {

	private final StaticAppConfigValues staticAppConfigValues;

	@Override
	public String queueUrl() {
		return this.staticAppConfigValues.getInterviewingEventsConsumer();
	}
}
