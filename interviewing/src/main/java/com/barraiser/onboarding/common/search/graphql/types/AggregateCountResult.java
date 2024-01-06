package com.barraiser.onboarding.common.search.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AggregateCountResult {
    private String fieldValue;
    private Long count;
}
