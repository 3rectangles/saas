/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ScheduleInterviewMutation implements NamedDataFetcher<Object> {
	private final GraphQLUtil graphQLUtil;
	private final InterviewSchedulingService interviewSchedulingService;

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final ScheduleInterviewInput input = this.graphQLUtil.getArgument(environment, Constants.CONTEXT_KEY_INPUT,
				ScheduleInterviewInput.class);
		log.info("schedule interview input: {}", input.toString());

		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final InterviewDAO interviewDAO = this.interviewSchedulingService.scheduleInterview(user, input);

		return DataFetcherResult.newResult()
				.data(interviewDAO)
				.build();
	}

	@Override
	public String name() {
		return "scheduleInterview";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}
}
