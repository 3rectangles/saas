/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.events;

import com.barraiser.ats_integrations.common.ATSIntegrationsStaticAppConfigValues;
import com.barraiser.commons.eventing.EventConsumer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCalendarEventsConsumer extends EventConsumer {

	private final ATSIntegrationsStaticAppConfigValues atsIntegrationsStaticAppConfigValues;

	@Override
	public String queueUrl() {
		return this.atsIntegrationsStaticAppConfigValues
				.getAtsCalendarEventsQueue();
	}
}
