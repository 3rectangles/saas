/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ATSIntegrationsStaticAppConfigValues {
	@Value("${aws.eventBus}")
	private String eventBus;

	@Value("${merge-dev.secretNames}")
	private String mergeDevSecretNames;

	@Value("${queue.ats-integrations-events-consumer}")
	private String atsIntegrationsEventsConsumer;

	@Value("${queue.ats-calendar-events}")
	private String atsCalendarEventsQueue;

	@Value("${lever.secretNames}")
	private String leverSecretNames;
}
