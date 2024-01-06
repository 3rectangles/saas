package com.barraiser.onboarding.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAvailabilityInput {
    private String userId;
    private List<AvailabilityInput> availabilities;
}
