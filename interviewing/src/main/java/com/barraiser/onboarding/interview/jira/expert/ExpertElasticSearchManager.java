/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.elasticsearch.config.client.BarraiserElasticsearchClient;
import com.barraiser.elasticsearch.config.client.ElasticSearchResponseHelper;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.search.BarraiserEsQueryHelper;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ExpertElasticSearchManager implements ExpertSearchManager {
	private final String EXPERT_INDEX_NAME = "expert";
	private final ExpertMapper expertMapper;
	private final BarraiserElasticsearchClient esClient;
	private final ExpertElasticSearchQueryManager expertElasticSearchQueryManager;
	private final BarraiserEsQueryHelper esQueryHelper;
	private final ElasticSearchResponseHelper elasticSearchResponseHelper;

	@Override
	public void updateExpertDetails(final ExpertDetails expertDetails) throws IOException {
		final ExpertSearchDAO expertSO = this.expertMapper.toExpertSearchDAO(expertDetails);
		this.save(expertSO);
	}

	@Override
	public SearchResponse searchExpertDetails(final MatchInterviewersData interviewersData) throws IOException {
		final QueryBuilder query;
		if (interviewersData.getFixedQuestionExperts() == null) {
			query = this.expertElasticSearchQueryManager.createQuery(interviewersData);
		} else {
			query = this.expertElasticSearchQueryManager.createQueryForFixedQuestions(interviewersData);
		}
		return this.searchByQuery(query);
	}

	@Override
	public DeleteResponse deleteExpertDetails(String index, String docId) throws IOException {
		final DeleteRequest deleteRequest = this.esClient.getDeleteRequest(index, docId);
		return esClient.delete(deleteRequest, null);
	}

	public ExpertSearchDAO findById(final String expertId) throws IOException {
		QueryBuilder query = QueryBuilders.boolQuery();
		query = this.esQueryHelper.eq(query, ExpertProps.ID, expertId);
		final SearchResponse searchResponse = this.searchByQuery(query);
		final List<ExpertSearchDAO> expertSearchDAOs = this.elasticSearchResponseHelper.parseResponse(searchResponse,
				ExpertSearchDAO.class);
		if (expertSearchDAOs.size() != 0) {
			return expertSearchDAOs.get(0);
		}
		return null;
	}

	private SearchResponse searchByQuery(final QueryBuilder query) throws IOException {
		final SearchRequest searchRequest = this.esClient.getSearchRequest(query,
				null, null, null, Constants.MAX_ES_RECORD_FETCH_SIZE);

		return this.esClient.searchOnIndex(EXPERT_INDEX_NAME,
				searchRequest, null);
	}

	public void save(final ExpertSearchDAO expertSO) throws IOException {
		final IndexRequest indexRequest = this.esClient.getIndexRequest(EXPERT_INDEX_NAME, expertSO.getUserId(),
				expertSO);
		this.esClient.index(indexRequest, RequestOptions.DEFAULT);
	}
}
