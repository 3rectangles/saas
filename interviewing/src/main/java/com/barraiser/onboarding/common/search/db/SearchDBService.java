package com.barraiser.onboarding.common.search.db;

public interface SearchDBService <T> {
    SearchResult<T> findAll(final SearchQuery searchQuery);
}
