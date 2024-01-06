/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreDAO;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.barraiser.common.graphql.input.CategoryCutoffInput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// tp-722 check where this class is used: class was mostly used for all kinds of BGS related decision making and test
@Log4j2
@Component
@RequiredArgsConstructor
public class BgsScoreFetcher {
	private final EvaluationRepository evaluationRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewScoreRepository interviewScoreRepository;
	private final ObjectMapper objectMapper;
	private final EvaluationScoreRepository evaluationScoreRepository;

	public Integer getBgsScoreForInterview(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final EvaluationDAO evaluation = this.evaluationRepository
				.findById(interviewDAO.getEvaluationId()).get();
		final List<InterviewScoreDAO> interviewScores = this.interviewScoreRepository
				.findAllByInterviewIdAndScoringAlgoVersion(interviewId, evaluation.getDefaultScoringAlgoVersion());
		return BgsCalculator.calculateBgsNoScale(interviewScores.stream().map(x -> this.objectMapper
				.convertValue(x, SkillScore.class)).collect(Collectors.toList()));
	}

	public Integer getBgsScoreForEvaluation(final String evaluationId) {
		final EvaluationDAO evaluation = this.evaluationRepository.findById(evaluationId).get();
		final List<EvaluationScoreDAO> evaluationScores = this.evaluationScoreRepository
				.findAllByEvaluationIdAndScoringAlgoVersionAndProcessType(evaluationId,
						evaluation.getDefaultScoringAlgoVersion(), InterviewProcessType.OVERALL);
		return BgsCalculator
				.calculateBgsNoScale(
						evaluationScores.stream().map(x -> this.objectMapper.convertValue(x, SkillScore.class))
								.collect(Collectors.toList()));
	}

	public Integer getBgsScoreForEvaluationBasedOnInterviewProcessType(
			final String evaluationId,
			final InterviewProcessType interviewProcessType) {
		final EvaluationDAO evaluation = this.evaluationRepository
				.findById(evaluationId)
				.get();
		final List<EvaluationScoreDAO> evaluationScores = this.evaluationScoreRepository
				.findAllByEvaluationIdAndScoringAlgoVersionAndProcessType(evaluationId,
						evaluation.getDefaultScoringAlgoVersion(), interviewProcessType);
		return BgsCalculator
				.calculateBgsNoScale(
						evaluationScores.stream().map(x -> this.objectMapper.convertValue(x, SkillScore.class))
								.collect(Collectors.toList()));
	}

	public Boolean isCategoryThresholdCleared(final String interviewId, final String categoryRejectionJSON) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final EvaluationDAO evaluation = this.evaluationRepository
				.findById(interviewDAO.getEvaluationId()).get();
		final List<InterviewScoreDAO> interviewScores = this.interviewScoreRepository
				.findAllByInterviewIdAndScoringAlgoVersion(interviewId, evaluation.getDefaultScoringAlgoVersion());

		final ObjectMapper mapper = new ObjectMapper();
		try {
			List<CategoryCutoffInput> categoryCutoffs = mapper.readValue(
					categoryRejectionJSON,
					new TypeReference<List<CategoryCutoffInput>>() {
					});
			return BgsCalculator.checkSkillCutoff(interviewScores.stream().map(x -> this.objectMapper
					.convertValue(x, SkillScore.class)).collect(Collectors.toList()), categoryCutoffs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
