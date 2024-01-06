/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.events;

import com.barraiser.commons.eventing.EventConsumer;
import com.barraiser.data_science.common.DataScienceStaticAppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class DataScienceConsumer extends EventConsumer {
	private final DataScienceStaticAppConfig dataScienceStaticAppConfig;

	@Override
	public String queueUrl() {
		return this.dataScienceStaticAppConfig.getDataScienceEventsConsumer();
	}
}
