/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.search;

import com.barraiser.commons.auth.SearchFilter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class BarraiserEsQueryHelper {

	public QueryBuilder getQueryForFilters(final List<SearchFilter> searchFilters) {

		/**
		 * Using the matchAll query by default incase the filters are empty
		 */
		final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.matchAllQuery());
		searchFilters.stream().forEach(sf -> queryBuilder.must(this.getTermFilter(sf)));
		return queryBuilder;
	}

	public QueryBuilder getTermFilter(final SearchFilter filter) {

		switch (filter.getOperator()) {
			case LIKE:
				return QueryBuilders.matchQuery(filter.getField(), ((ArrayList<String>) filter.getValue()).get(0));
			case EQUALS:
				return QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(filter.getField(), filter.getValue()));
			case IN:
				return QueryBuilders.boolQuery()
						.filter(QueryBuilders.termsQuery(filter.getField(), (ArrayList<Object>) filter.getValue()));
			case NOT_EQUALS:
				return QueryBuilders.boolQuery()
						.mustNot(QueryBuilders.termsQuery(filter.getField(), filter.getValue()));
			default:
				throw new NoSuchElementException(
						"Filter Operator with value " + filter.getOperator() + " has not been found");
		}
	}

	public QueryBuilder eq(QueryBuilder queryBuilder, String key, Object value) {
		return QueryBuilders.boolQuery().must(queryBuilder).must(QueryBuilders.termsQuery(key, value));
	}

	public QueryBuilder notEq(QueryBuilder queryBuilder, String key, Object value) {
		return QueryBuilders.boolQuery().must(queryBuilder).mustNot(QueryBuilders.termsQuery(key, value));
	}

	public QueryBuilder in(QueryBuilder queryBuilder, String key, List<Object> value) {
		return QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery(key, value)).must(queryBuilder);
	}

	public QueryBuilder gte(QueryBuilder queryBuilder, String key, Object value) {
		return QueryBuilders.boolQuery().must(queryBuilder).must(QueryBuilders.rangeQuery(key).gte(value));
	}
}
