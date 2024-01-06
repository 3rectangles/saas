/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.FixedQuestionsUsersDAO;
import com.barraiser.onboarding.dal.FixedQuestionsUsersRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertFixedQuestionMatchingProcessor implements MatchInterviewersProcessor {
	private final ExpertDataTransformer expertDataTransformer;
	private final InterviewerCostPopulator interviewerCostPopulator;
	private final ExpertElasticSearchManager expertSearchManager;
	private final FixedQuestionsUsersRepository fixedQuestionsUsersRepository;
	private final ElasticSearchResponseHelper elasticSearchResponseHelper;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {

		final Set<String> expertIds = this.extractExpertIds(data);
		data.setFixedQuestionExperts(new ArrayList<String>(expertIds));

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

	private Set<String> extractExpertIds(final MatchInterviewersData data) {
		// initialize questionMap from data
		final Map<String, String[]> questionMap = data.getFixedQuestionMap();

		// Initialize a set to store expert IDs that are linked to at least one question
		// in all lists
		Set<String> commonExpertIds = new HashSet<>();

		// Loop through each question list in the questionMap
		int count = 0;
		for (String[] questionIds : questionMap.values()) {

			// Query the database to find all FixedQuestionsUsersDAO objects with the
			// current question IDs
			List<FixedQuestionsUsersDAO> daoList = fixedQuestionsUsersRepository
					.findByQuestionIdIn(Arrays.asList(questionIds));

			// Initialize a set to store expert IDs that are linked to the current question
			// list
			Set<String> expertIds = daoList.stream().map(FixedQuestionsUsersDAO::getExpertId)
					.collect(Collectors.toSet());

			// If this is the first question list, add all expert IDs to the common set
			if (commonExpertIds.isEmpty() && (count == 0)) {
				commonExpertIds.addAll(expertIds);
			} else {
				// Otherwise, retain only the expert IDs that are present in both sets
				commonExpertIds.retainAll(expertIds);
			}
			count++;
		}

		// Convert the set of common expert IDs to a list and return it
		return commonExpertIds;
	}
}