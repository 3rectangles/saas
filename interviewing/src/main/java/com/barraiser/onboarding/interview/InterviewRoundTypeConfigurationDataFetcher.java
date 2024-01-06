/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.InterviewRoundTypeConfiguration;
import com.barraiser.onboarding.dal.InterviewRoundTypeConfigurationDAO;
import com.barraiser.onboarding.dal.InterviewRoundTypeConfigurationRepository;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class InterviewRoundTypeConfigurationDataFetcher implements MultiParentTypeDataFetcher {
	private final InterviewRoundTypeConfigurationRepository interviewRoundTypeConfigurationRepository;
	private final JobRoleManager jobRoleManager;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "roundTypeConfiguration"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals("Interview")) {
			final Interview interview = environment.getSource();
			final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRole(
					interview.getJobRoleId(), interview.getJobRoleVersion()).get();
			final InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO = this
					.getInterviewRoundConfig(interview.getInterviewRound(), jobRoleDAO.getCompanyId());
			return DataFetcherResult.newResult().data(InterviewRoundTypeConfiguration.builder()
					.commonZoomLink(interviewRoundTypeConfigurationDAO.getCommonZoomLink())
					.build()).build();
		}

		throw new IllegalArgumentException();
	}

	private InterviewRoundTypeConfigurationDAO getInterviewRoundConfig(final String interviewRound,
			final String companyId) {
		Optional<InterviewRoundTypeConfigurationDAO> roundTypeConfigurationDAOOptional = this.interviewRoundTypeConfigurationRepository
				.findByRoundTypeAndCompanyId(interviewRound, companyId);

		if (roundTypeConfigurationDAOOptional.isEmpty()) {
			roundTypeConfigurationDAOOptional = this.interviewRoundTypeConfigurationRepository
					.findByRoundType(interviewRound);
		}
		return roundTypeConfigurationDAOOptional.get();
	}
}
