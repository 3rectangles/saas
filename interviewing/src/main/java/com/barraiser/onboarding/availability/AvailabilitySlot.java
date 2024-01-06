package com.barraiser.onboarding.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AvailabilitySlot {
    private int index;
    private String date;
    private Long startDate;
    private Long endDate;
    private String formattedStartTime;
    private String formattedEndTime;
}
