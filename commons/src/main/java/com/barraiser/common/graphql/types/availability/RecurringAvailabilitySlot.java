/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RecurringAvailabilitySlot {

	private DayOfTheWeek dayOfTheWeek;

	private Integer maxInterviewsInSlot;

	private Integer startTime;

	private Integer endTime;
}
