package com.barraiser.onboarding.common.search.db;

import com.barraiser.common.graphql.input.SearchOrderInput;

import java.util.List;

public interface SearchOrderMapper <T> {
    String field();

    List<SearchOrder> getSearchOrders(final SearchOrderInput searchOrderInput);
}
