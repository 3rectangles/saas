/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.InterviewingData;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interviewing.notes.dal.InterviewingDataDAO;
import com.barraiser.onboarding.interviewing.notes.dal.InterviewingDataRepository;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewingDataFetcher implements MultiParentTypeDataFetcher {
	private final InterviewingDataRepository interviewingDataRepository;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "interviewingData"));
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();
		final InterviewingDataDAO interviewingData = this.interviewingDataRepository
				.findByInterviewId(interview.getId()).orElse(
						InterviewingDataDAO.builder().build());
		return DataFetcherResult.newResult().data(
				InterviewingData.builder()
						.wasInterviewerVideoOn(interviewingData.getWasInterviewerVideoOn())
						.build())
				.build();
	}
}
