/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.types.expertProfile.ExpertInterviewingConfiguration;
import com.barraiser.common.graphql.types.expertProfile.ExpertProfile;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.expert.auth.ExpertInterviewingConfigurationAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpertInterviewingConfigurationDataFetcher
		extends AuthorizedGraphQLQuery_deprecated<ExpertInterviewingConfiguration> {

	private final ExpertRepository expertRepository;

	public ExpertInterviewingConfigurationDataFetcher(
			ExpertInterviewingConfigurationAuthorizer expertInterviewingConfigurationAuthorizer,
			ObjectFieldsFilter<ExpertInterviewingConfiguration> objectFieldsFilter, ExpertRepository expertRepository) {
		super(expertInterviewingConfigurationAuthorizer, objectFieldsFilter);
		this.expertRepository = expertRepository;
	}

	@Override
	protected ExpertInterviewingConfiguration fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		ExpertProfile expertProfile = environment.getSource();
		return this.getConfiguration(expertProfile.getExpertId());
	}

	private ExpertInterviewingConfiguration getConfiguration(final String expertId) {
		final ExpertDAO expertDAO = this.expertRepository.findById(expertId).orElse(ExpertDAO.builder().build());

		return ExpertInterviewingConfiguration.builder()
				.timeGapBetweenInterviews(expertDAO.getGapBetweenInterviews())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("ExpertProfile", "interviewingConfiguration"));
	}
}
