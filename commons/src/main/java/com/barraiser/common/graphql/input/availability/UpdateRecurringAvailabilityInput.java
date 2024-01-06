/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input.availability;

import com.barraiser.common.graphql.types.availability.RecurringAvailabilitySlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateRecurringAvailabilityInput {

	private String userId;

	private String timezone;

	private List<RecurringAvailabilitySlot> slots;

}
