package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetAvailabilityInput {
    private String userId;
    private long startDate;
    private long endDate;
    @Builder.Default
    private boolean breakIntoSlots = true;
}
