/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.config;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration

public class ExpertReassignmentConfigFactory {
	public static final String INTERVIEW_RESCHEDULING_CONFIG = "interview_rescheduling_config";
	private final DynamicAppConfigProperties appConfigProperties;

	@Bean
	public ExpertReassignmentConfig getInterviewReschedulingConfig()
			throws JsonProcessingException {
		return this.appConfigProperties.getObject(
				INTERVIEW_RESCHEDULING_CONFIG, ExpertReassignmentConfig.class);
	}

}
