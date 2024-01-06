/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.scores.NormalisationVersionFetcher;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class NormalisedRatingPopulator {
	public static final List<Double> FEEDBACK_RATINGS = List.of(1D, 2D, 3D, 4D, 5D, 6D, 7D, 8D, 9D, 10D);

	private final ExpertNormalisedRatingRepository expertNormalisedRatingRepository;
	private final FeedbackRepository feedbackRepository;
	private final QuestionRepository questionRepository;
	private final ObjectMapper objectMapper;

	public void populate(final InterviewDAO interviewDAO) {
		final List<NormalisedRatingMapping> ratingToNormalisedRatingMapping = this
				.getRatingToNormalisedRatingOfExpert(interviewDAO.getInterviewerId());
		final List<FeedbackData> feedbacks = this.getFeedbacksForInterview(interviewDAO.getId());
		final List<FeedbackData> feedbacksWithNormalisedRatings = this.getFeedbacksWithNormalisedRatings(feedbacks,
				ratingToNormalisedRatingMapping);
		this.saveFeedback(feedbacksWithNormalisedRatings);
	}

	private List<FeedbackData> getFeedbacksForInterview(final String interviewId) {
		final List<QuestionDAO> questions = this.questionRepository.findAllByInterviewId(interviewId);
		final List<FeedbackDAO> feedbacksOnQuestionLevel = this.feedbackRepository.findByReferenceIdIn(
				questions.stream().map(QuestionDAO::getId).collect(Collectors.toList()));
		final List<FeedbackDAO> feedbacksOnInterviewLevel = this.feedbackRepository.findAllByReferenceIdAndType(
				interviewId,
				Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS);
		final List<FeedbackDAO> allFeedbacks = feedbacksOnQuestionLevel;
		allFeedbacks.addAll(feedbacksOnInterviewLevel);
		return allFeedbacks.stream().map(x -> this.objectMapper.convertValue(x, FeedbackData.class))
				.collect(Collectors.toList());
	}

	private void saveFeedback(final List<FeedbackData> feedbackData) {
		final List<FeedbackDAO> feedbacksToBeSaved = feedbackData.stream()
				.map(x -> this.objectMapper.convertValue(x, FeedbackDAO.class))
				.collect(Collectors.toList());
		this.feedbackRepository.saveAll(feedbacksToBeSaved);
	}

	public List<NormalisedRatingMapping> getRatingToNormalisedRatingOfExpert(final String interviewerId) {
		final List<ExpertNormalisedRatingDAO> expertNormalisedRatingDAOs = this.expertNormalisedRatingRepository
				.findAllByInterviewerId(interviewerId);
		final List<NormalisedRatingMapping> normalisedRatingMappings = new ArrayList<>();
		normalisedRatingMappings.addAll(this.getDefaultRatings());
		if (expertNormalisedRatingDAOs.size() > 0) {
			expertNormalisedRatingDAOs.forEach(
					x -> normalisedRatingMappings.add(NormalisedRatingMapping.builder()
							.rating(x.getRating().floatValue())
							.cappedNormalisedRating(x.getCappedNormalisedRating().floatValue())
							.normalisationVersion(x.getNormalisationVersion())
							.normalisedRating(x.getNormalisedRating().floatValue())
							.build()));
		} else {
			NormalisationVersionFetcher.normalisationVersions.forEach(
					y -> FEEDBACK_RATINGS.forEach(x -> normalisedRatingMappings.add(NormalisedRatingMapping.builder()
							.rating(x.floatValue())
							.cappedNormalisedRating(x.floatValue())
							.normalisationVersion(y)
							.normalisedRating(x.floatValue())
							.build())));
		}
		return normalisedRatingMappings;
	}

	public List<FeedbackData> getFeedbacksWithNormalisedRatings(List<FeedbackData> feedbacks,
			final List<NormalisedRatingMapping> normalisedRatingMappings) {
		feedbacks = feedbacks.stream().map(x -> {
			if (x.getRating() != null) {
				final List<NormalisedRatingMapping> normalisedRatingMapping;
				if (Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS.equals(x.getType())) {
					normalisedRatingMapping = normalisedRatingMappings.stream()
							.filter(y -> y.getRating().equals(x.getRating()))
							.map(y -> y.toBuilder().cappedNormalisedRating(y.getCappedNormalisedRating() * 2 / 2F)
									.build())
							.collect(Collectors.toList());
				} else {
					normalisedRatingMapping = normalisedRatingMappings.stream()
							.filter(y -> y.getRating().equals(x.getRating())).collect(Collectors.toList());
				}
				return x.toBuilder().normalisedRatingMappings(normalisedRatingMapping).build();
			}
			return x;
		}).collect(Collectors.toList());
		return feedbacks;
	}

	private List<NormalisedRatingMapping> getDefaultRatings() {
		final List<NormalisedRatingMapping> normalisedRatingMappings = new ArrayList<>();
		NormalisationVersionFetcher.normalisationVersions
				.forEach(x -> normalisedRatingMappings.add(NormalisedRatingMapping.builder()
						.rating(0F)
						.cappedNormalisedRating(0F)
						.normalisationVersion(x)
						.normalisedRating(0F)
						.build()));
		return normalisedRatingMappings;
	}
}
