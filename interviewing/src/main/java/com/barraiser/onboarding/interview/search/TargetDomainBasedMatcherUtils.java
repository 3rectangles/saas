/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.search;

import com.barraiser.elasticsearch.config.client.ElasticSearchResponseHelper;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TargetDomainBasedMatcherUtils {
	private final ElasticSearchResponseHelper elasticSearchResponseHelper;

	List<String> fetchInterviewersFromElasticSearch(
			final SearchResponse response)
			throws JsonProcessingException {
		return toInterviewerIds(this.elasticSearchResponseHelper.parseResponse(response, ExpertSearchDAO.class));
	}

	private List<String> toInterviewerIds(final List<ExpertSearchDAO> expertSearchDAOS) {
		return expertSearchDAOS.stream()
				.map(ExpertSearchDAO::getUserId).collect(Collectors.toList());
	}
}
