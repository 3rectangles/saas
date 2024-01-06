package com.barraiser.onboarding.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AvailabilityInput {
    private Long startDate;
    private Long endDate;
    private Integer maximumNumberOfInterviews;
}
