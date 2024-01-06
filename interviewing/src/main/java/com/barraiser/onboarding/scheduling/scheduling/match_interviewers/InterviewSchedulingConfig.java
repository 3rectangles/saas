/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewSchedulingConfig {
	@JsonProperty("scheduled_time_lower_bound")
	private Long scheduledTimeLowerBound;

	@JsonProperty("scheduled_time_upper_bound")
	private Long scheduledTimeUpperBound;
}
