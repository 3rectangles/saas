/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class InterviewSchedulingConfigFactory {

	public static final String INTERVIEW_SCHEDULING_CONFIG = "interview_scheduling_config";
	private final DynamicAppConfigProperties appConfigProperties;

	@Bean
	public InterviewSchedulingConfig getInterviewSchedulingConfig()
			throws JsonProcessingException {
		return this.appConfigProperties.getObject(
				INTERVIEW_SCHEDULING_CONFIG, InterviewSchedulingConfig.class);
	}
}
