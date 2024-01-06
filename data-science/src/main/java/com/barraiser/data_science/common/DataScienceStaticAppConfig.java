/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DataScienceStaticAppConfig {
	@Value("${queue.data-science-events-consumer}")
	private String dataScienceEventsConsumer;
}
