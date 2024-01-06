/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.events;

import com.barraiser.ats_integrations.common.ATSIntegrationsStaticAppConfigValues;
import com.barraiser.commons.eventing.EventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class ATSIntegrationsEventProducer extends EventProducer {
	private static final String SOURCE = "barraiser.ats_integrations";

	private final ATSIntegrationsStaticAppConfigValues staticAppConfigValues;

	@Override
	public String eventBus() {
		return this.staticAppConfigValues.getEventBus();
	}

	@Override
	public String source() {
		return SOURCE;
	}
}
