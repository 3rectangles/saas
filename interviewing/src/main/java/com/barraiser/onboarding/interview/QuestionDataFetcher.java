/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.barraiser.onboarding.config.ConfigComposer;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class QuestionDataFetcher implements NamedDataFetcher {
	private final QuestionRepository questionRepository;
	private final InterViewRepository interViewRepository;
	private final ObjectMapper objectMapper;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final ConfigComposer configComposer;

	@Override
	public String name() {
		return "questions";
	}

	@Override
	public String type() {
		return "Interview";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final Interview interview = environment.getSource();
		final Long videoStartTime = interview.getVideoStartTime();

		final InterviewDAO interviewDAO = this.interViewRepository.findById(interview.getId()).get();
		final List<QuestionDAO> questionDAOS = this.questionRepository
				.findAllByInterviewIdAndMasterQuestionIdNullAndRescheduleCountOrderByStartTimeEpochAsc(
						interview.getId(),
						interviewDAO.getRescheduleCount());

		final List<Question> questions = questionDAOS.stream().map(x -> {
			final Question question = this.objectMapper.convertValue(x, Question.class);

			if (x.getStartTimeEpoch() != null && videoStartTime != null) {
				return question.toBuilder().startTime(x.getStartTimeEpoch() - videoStartTime).build();
			}
			return question;

		}).collect(Collectors.toList());

		return DataFetcherResult.newResult()
				.data(questions)
				.build();
	}
}
