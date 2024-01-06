/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.expert.InterviewerCostPopulator;
import com.barraiser.elasticsearch.config.client.ElasticSearchResponseHelper;
import com.barraiser.onboarding.interview.jira.expert.ExpertDataTransformer;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertElasticSearchMatchingProcessor implements MatchInterviewersProcessor {
	private final ExpertDataTransformer expertDataTransformer;
	private final ExpertElasticSearchManager expertSearchManager;
	private final InterviewerCostPopulator interviewerCostPopulator;
	private final ElasticSearchResponseHelper elasticSearchResponseHelper;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {
		final SearchResponse searchResponse = this.expertSearchManager.searchExpertDetails(data);
		final List<InterviewerData> interviewerData = this.getInterviewersData(searchResponse,
				data.getWorkExperienceOfIntervieweeInMonths());
		data.setInterviewers(this.interviewerCostPopulator.populateTotalCostInINR(interviewerData,
				data.getDurationOfInterview(), data.getExpertJoiningTime()));
	}

	private List<InterviewerData> getInterviewersData(
			final SearchResponse response, final Integer workExperienceOfIntervieweeInMonths)
			throws JsonProcessingException {
		return this.expertDataTransformer.toInterviewerData(
				this.elasticSearchResponseHelper.parseResponse(response, ExpertSearchDAO.class),
				workExperienceOfIntervieweeInMonths);
	}
}
