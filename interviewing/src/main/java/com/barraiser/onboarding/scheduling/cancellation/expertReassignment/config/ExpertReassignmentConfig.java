/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExpertReassignmentConfig {
	@JsonProperty("scheduled_time_lower_bound")
	private Long scheduledTimeLowerBound;

	@JsonProperty("scheduled_time_upper_bound")
	private Long scheduledTimeUpperBound;

	@JsonProperty("cancellation_time_lower_bound")
	private Long cancellationTimeLowerBound;

	@JsonProperty("cancellation_time_upper_bound")
	private Long cancellationTimeUpperBound;

	@JsonProperty("min_time_for_system_to_reassign_expert")
	private Long minTimeForSystemToReassignExpert;

	@JsonProperty("max_duration_to_allow_expert_reassignment")
	private Long maxDurationToAllowExpertReassignment;

}
