package com.barraiser.onboarding.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AddAvailabilityInput {
    private String userId;
    private List<AvailabilityInput> availabilities;
}
