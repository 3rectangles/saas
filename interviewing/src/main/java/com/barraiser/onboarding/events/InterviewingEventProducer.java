/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events;

import com.barraiser.commons.eventing.EventProducer;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class InterviewingEventProducer extends EventProducer {
	private final StaticAppConfigValues staticAppConfigValues;

	@Override
	public String eventBus() {
		return this.staticAppConfigValues.getEventBus();
	}

	@Override
	public String source() {
		return "barraiser.interviewing";
	}
}
