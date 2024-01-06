/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.elasticsearch.config.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ElasticSearchResponseHelper {
	private final ObjectMapper objectMapper;

	public <T> List<T> parseResponse(final SearchResponse searchResponse, final Class<T> valueType)
			throws JsonProcessingException {
		final SearchHits hits = searchResponse.getHits();
		final List<T> list = new ArrayList<>();
		for (final SearchHit hit : hits.getHits()) {
			final String hitJson = hit.getSourceAsString();
			list.add(this.objectMapper.readValue(hitJson, valueType));
		}
		return list;
	}
}
