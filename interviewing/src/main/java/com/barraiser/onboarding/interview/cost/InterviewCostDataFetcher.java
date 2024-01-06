/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.cost;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCostDataFetcher implements NamedDataFetcher<Object> {

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();

		return environment
				.getDataLoader(InterviewCostDataLoaderFactory.DATA_LOADER_NAME)
				.load(
						InterviewCostCriteria.builder()
								.interviewId(interview.getId())
								.rescheduleCount(interview.getRescheduleCount())
								.interviewerId(interview.getInterviewerId())
								.build());
	}

	@Override
	public String name() {
		return "cost";
	}

	@Override
	public String type() {
		return "Interview";
	}
}
