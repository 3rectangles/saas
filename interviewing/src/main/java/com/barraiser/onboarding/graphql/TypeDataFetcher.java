package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetcher;

public interface TypeDataFetcher<T> extends DataFetcher<T> {
    String name();

    default DataFetcherType getType() {
        return DataFetcherType.TYPE;
    }
}
