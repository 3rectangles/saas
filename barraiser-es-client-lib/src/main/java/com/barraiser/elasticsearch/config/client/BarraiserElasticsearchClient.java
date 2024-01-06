/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.elasticsearch.config.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component

public class BarraiserElasticsearchClient {

	private RestHighLevelClient esCLient;
	private ObjectMapper objectMapper;

	/**
	 * @param queryBuilder
	 * @param sortingCriteria
	 * @param sortingOrder
	 * @return
	 */
	public SearchRequest getSearchRequest(final QueryBuilder queryBuilder, final AggregationBuilder aggregationBuilder,
			final String sortingCriteria, final SortOrder sortingOrder, int size) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(size);

		if (queryBuilder != null) {
			searchSourceBuilder.query(queryBuilder);
		}

		if (sortingOrder != null && sortingCriteria != null) {
			searchSourceBuilder.sort(sortingCriteria, sortingOrder);
		}

		if (aggregationBuilder != null) {
			searchSourceBuilder.aggregation(aggregationBuilder);
		}

		final SearchRequest searchRequest = new SearchRequest().source(searchSourceBuilder);
		return searchRequest;
	}

	/**
	 * @param index
	 * @param searchRequest
	 * @param requestOptions
	 * @return
	 * @throws IOException
	 */
	public SearchResponse searchOnIndex(final String index, final SearchRequest searchRequest,
			final RequestOptions requestOptions) throws IOException {
		final RequestOptions reqOptions = requestOptions == null ? RequestOptions.DEFAULT : requestOptions;
		searchRequest.indices(index);
		return this.esCLient.search(searchRequest, reqOptions);
	}

	/**
	 * @param indexRequest
	 * @param requestOptions
	 * @return
	 * @throws IOException
	 */
	public IndexResponse index(final IndexRequest indexRequest, final RequestOptions requestOptions)
			throws IOException {
		final RequestOptions reqOptions = requestOptions == null ? RequestOptions.DEFAULT : requestOptions;
		return this.esCLient.index(indexRequest, reqOptions);
	}

	public DeleteResponse delete(final DeleteRequest deleteRequest, final RequestOptions requestOptions)
			throws IOException {
		final RequestOptions reqOption = requestOptions == null ? RequestOptions.DEFAULT : requestOptions;
		return this.esCLient.delete(deleteRequest, reqOption);
	}

	/**
	 * @param index
	 *            name of index
	 * @param documentId
	 *            unique document id
	 * @param sourceData
	 * @return
	 * @throws JsonProcessingException
	 */

	public IndexRequest getIndexRequest(final String index, final String documentId, final Object sourceData)
			throws JsonProcessingException {
		final IndexRequest indexRequest = new IndexRequest(index);
		indexRequest.id(documentId);
		indexRequest.source(this.objectMapper.writeValueAsString(sourceData), XContentType.JSON);
		return indexRequest;
	}

	/**
	 *
	 * @param index
	 *            name of index
	 * @param documentId
	 *            unique document id
	 * @return
	 */

	public DeleteRequest getDeleteRequest(final String index, final String documentId) {
		return new DeleteRequest(index, documentId);
	}
}
