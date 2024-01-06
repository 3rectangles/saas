/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.search;

import com.barraiser.elasticsearch.config.client.BarraiserElasticsearchClient;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.interview.GetInterviewers;
import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.interview.jira.expert.ExpertProps;

import com.barraiser.onboarding.search.BarraiserEsQueryHelper;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TargetDomainBasedMatcherES implements InterviewerMatcher {
	private final String EXPERT_FILTER_INDEX = "expert";
	private final BarraiserEsQueryHelper esQueryHelper;
	private final BarraiserElasticsearchClient esClient;
	private final TargetDomainBasedMatcherUtils utils;

	@Override
	public List<String> getInterviewers(Map<String, String> parameters) throws IOException {
		final String domain = parameters.get(GetInterviewers.DOMAIN);
		final String interviewRound = parameters.get(GetInterviewers.INTERVIEW_ROUND);
		final String targetCompany = parameters.get(GetInterviewers.TARGET_COMPANY);
		final Boolean isInternalInterview = Constants.ROUND_TYPE_INTERNAL.equals(interviewRound);

		final String companyForWhichExpertCanTakeInterview = isInternalInterview ? targetCompany
				: ExpertProps.COMPANY_BARRAISER_EXPERT_ID;
		QueryBuilder query = QueryBuilders.boolQuery();
		query = this.esQueryHelper.eq(query, ExpertProps.COMPANIES_FOR_WHICH_EXPERT_CAN_TAKE_INTERVIEW,
				companyForWhichExpertCanTakeInterview);
		if (RoundType.PEER.getValue().equals(interviewRound) && !isInternalInterview) {
			query = this.esQueryHelper.eq(query, ExpertProps.PEER_DOMAINS, domain);
		} else if (RoundType.EXPERT.getValue().equals(interviewRound) && !isInternalInterview) {
			query = this.esQueryHelper.eq(query, ExpertProps.EXPERT_DOMAINS, domain);
		} else if (interviewRound == null || interviewRound.isEmpty()) {
			throw new IllegalArgumentException("Interview round must not be empty.");
		}
		final SearchResponse response = search(query);
		return this.utils.fetchInterviewersFromElasticSearch(response);
	}

	private SearchResponse search(final QueryBuilder query) throws IOException {
		final SearchRequest searchRequest = this.esClient.getSearchRequest(query,
				null, null, null, Constants.MAX_ES_RECORD_FETCH_SIZE);
		return this.esClient.searchOnIndex(EXPERT_FILTER_INDEX,
				searchRequest, null);
	}

}
