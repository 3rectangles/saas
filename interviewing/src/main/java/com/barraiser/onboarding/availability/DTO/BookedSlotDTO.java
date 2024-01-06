/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.DTO;

import com.barraiser.onboarding.availability.enums.BookingSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookedSlotDTO {
	private String id;

	private Long startDate;

	private Long endDate;

	private Long buffer;

	private BookingSource source;

	private String userId;
}
