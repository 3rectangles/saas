package com.barraiser.onboarding.common.search.db;

import com.barraiser.common.graphql.input.AggregateInput;

public interface SearchAggregationMapper <T> {
    String field();

    SearchAggregation getSearchAggregation(final AggregateInput aggregateInput);

}
