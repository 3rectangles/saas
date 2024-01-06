package com.barraiser.onboarding.common.search.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AggregateResultType {
    private List<AggregateCountResult> aggregatedCount;
}
