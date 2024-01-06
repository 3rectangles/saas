/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.DTO;

import com.barraiser.onboarding.availability.enums.BookingSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetBookedSlotsRequestDTO {
	private List<String> userIds;

	private Long startDate;

	private Long endDate;

	private BookingSource source;

	private OverlappingType overlappingType;

	private Boolean excludeBufferForOverlappingCheck;

	public enum OverlappingType {
		PARTIAL, COMPLETE, EXACT;
	}
}
