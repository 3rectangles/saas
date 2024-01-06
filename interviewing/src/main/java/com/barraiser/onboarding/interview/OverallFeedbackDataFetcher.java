/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.InterviewerRecommendation;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.FeedbackDAO;
import com.barraiser.onboarding.dal.FeedbackRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewerRecommendationDAO;
import com.barraiser.onboarding.dal.InterviewerRecommendationRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.OverallFeedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class OverallFeedbackDataFetcher implements NamedDataFetcher {
	private final FeedbackRepository feedbackRepository;
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final InterviewerRecommendationRepository interviewerRecommendationRepository;

	@Override
	public String name() {
		return "overallFeedback";
	}

	@Override
	public String type() {
		return "Interview";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interview.getId()).get();

		final FeedbackDAO strengthOverallFeedbackDAO = this.feedbackRepository
				.findByReferenceIdAndTypeAndRescheduleCount(
						interview.getId(), Constants.OVERALL_FEEDBACK_TYPE_STRENGTH, interviewDAO.getRescheduleCount());
		final FeedbackDAO areasOfImprovementOverallFeedbackDAO = this.feedbackRepository
				.findByReferenceIdAndTypeAndRescheduleCount(
						interview.getId(), Constants.OVERALL_FEEDBACK_TYPE_AREAS_OF_IMPROVEMENT,
						interviewDAO.getRescheduleCount());
		final List<FeedbackDAO> softSkillsFeedback = this.feedbackRepository
				.findAllByReferenceIdAndTypeAndRescheduleCount(
						interview.getId(), Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS,
						interviewDAO.getRescheduleCount());
		final InterviewerRecommendationDAO interviewerRecommendationDAO = this.interviewerRecommendationRepository
				.findByInterviewId(interview.getId()).orElse(InterviewerRecommendationDAO.builder().build());

		final OverallFeedback overallFeedback = OverallFeedback.builder()
				.areasOfImprovement(
						this.objectMapper.convertValue(areasOfImprovementOverallFeedbackDAO, Feedback.class))
				.strength(this.objectMapper.convertValue(strengthOverallFeedbackDAO, Feedback.class))
				.softSkills(softSkillsFeedback.stream().map(x -> this.objectMapper.convertValue(x, Feedback.class))
						.collect(Collectors.toList()))
				.interviewerRecommendation(InterviewerRecommendation.builder()
						.hiringRating(interviewerRecommendationDAO.getHiringRating())
						.remarks(interviewerRecommendationDAO.getRemarks())
						.cheatingSuspectedRemarks(interviewerRecommendationDAO.getCheatingSuspectedRemarks())
						.interviewIncompleteRemarks(interviewerRecommendationDAO.getInterviewIncompleteRemarks())
						.build())
				.build();

		return DataFetcherResult.newResult()
				.data(overallFeedback)
				.build();
	}
}
