package com.barraiser.onboarding.common.search.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AggregationResult {
    private List<AggregationCountResult> aggregatedCount;
    private String name;

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class AggregationCountResult {
        private Object fieldValue;
        private Long count;
    }
}
