/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.InterviewConfirmationDAO;
import com.barraiser.onboarding.dal.InterviewConfirmationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class InterviewConfirmationDataFetcher implements NamedDataFetcher {
	private final InterviewConfirmationRepository interviewConfirmationRepository;
	private final InterViewRepository interViewRepository;

	@Override
	public String name() {
		return "confirmation";
	}

	@Override
	public String type() {
		return "Interview";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();

		final InterviewDAO interviewDAO = this.interViewRepository.findById(interview.getId()).get();
		final InterviewConfirmationDAO interviewConfirmationDAO = this.interviewConfirmationRepository
				.findTopByInterviewIdAndRescheduleCountOrderByCandidateConfirmationTimeDesc(interview.getId(),
						interviewDAO.getRescheduleCount())
				.orElse(InterviewConfirmationDAO.builder().build());

		return DataFetcherResult.newResult()
				.data(interviewConfirmationDAO)
				.build();
	}
}
