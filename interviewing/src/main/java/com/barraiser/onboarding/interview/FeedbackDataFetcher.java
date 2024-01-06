/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.dal.FeedbackDAO;
import com.barraiser.onboarding.dal.FeedbackRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V7;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FeedbackDataFetcher implements NamedDataFetcher {
	private final FeedbackRepository feedbackRepository;
	private final ObjectMapper objectMapper;
	private final FeedbackNormalisationUtil feedbackNormalisationUtil;
	private final ConfigComposer configComposer;
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public String name() {
		return "feedbacks";
	}

	@Override
	public String type() {
		return "Question";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Question question = environment.getSource();
		final List<FeedbackDAO> feedbacksDAO = this.feedbackRepository.findByReferenceId(question.getId());

		final InterviewDAO interviewDAO = this.interViewRepository.findById(question.getInterviewId()).get();
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(interviewDAO.getPartnerId())
				.get();
		final Boolean isSaasInterview = interviewUtil.isSaasInterview(interviewDAO.getInterviewRound());

		List<FeedbackData> feedbacks = feedbacksDAO.stream()
				.map(x -> this.objectMapper.convertValue(x, FeedbackData.class).toBuilder()
						.isSaasFeedback(isSaasInterview).build())
				.collect(Collectors.toList());
		if (environment.getLocalContext() != null) {
			final String algoVersion = ((Map<String, String>) environment.getLocalContext()).get("scoringAlgo");
			feedbacks = feedbacks.stream().map(x -> x.toBuilder()
					.modifiedRating(this.getDisplayableNormalisedRating(x, algoVersion, partnerCompanyDAO))
					.build()).collect(Collectors.toList());
		}
		return DataFetcherResult.newResult().data(feedbacks.stream()
				.map(x -> this.objectMapper.convertValue(x, Feedback.class)).collect(Collectors.toList())).build();
	}

	private Float getDisplayableNormalisedRating(final FeedbackData feedback,
			final String algoVersion, final PartnerCompanyDAO partnerCompanyDAO) {
		final Double factor;
		if (this.shouldConsiderNormalisedRating(algoVersion, feedback, partnerCompanyDAO)) {
			final Float cappedNormalisedRating = this.feedbackNormalisationUtil
					.getCappedNormalisedRatingByScoringAlgoVersion(
							this.objectMapper.convertValue(feedback, FeedbackData.class), algoVersion);
			if (cappedNormalisedRating != null) {
				if (cappedNormalisedRating - feedback.getRating() > 0) {
					factor = Math.floor(cappedNormalisedRating.doubleValue()) -
							feedback.getRating();
				} else {
					factor = Math.ceil(cappedNormalisedRating.doubleValue()) - feedback.getRating();
				}
				return feedback.getRating() + factor.floatValue();
			} else {
				return null;
			}
		} else {
			return feedback.getRating();
		}
	}

	private boolean shouldConsiderNormalisedRating(final String algoVersion, final FeedbackData feedback,
			final PartnerCompanyDAO partnerCompanyDAO) {
		Boolean normalisationFlag = false;

		List<String> tags = new ArrayList<>();
		if (feedback.getIsSaasFeedback() == null)
			return false;

		tags.add(feedback.getIsSaasFeedback() ? "interview_type.internal" : "interview_type.br");

		try {
			JsonNode config = configComposer.compose("scoring_rating", tags);
			JsonNode configScoringBGS = config != null ? config.get("scoring_bgs") : null;
			normalisationFlag = configScoringBGS != null && configScoringBGS.get("normalizaton").asBoolean();
		} catch (Exception e) {
			normalisationFlag = false;
		}
		if (partnerCompanyDAO.getIsNormalisationEnabled() != null) {
			normalisationFlag = partnerCompanyDAO.getIsNormalisationEnabled();
		}
		return normalisationFlag
				&& Integer.parseInt(algoVersion) >= Integer.parseInt(EvaluationStrategy_V7.SCORING_ALGO_VERSION);
	}
}
