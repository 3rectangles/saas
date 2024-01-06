/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.evaluation.percentile.EvaluationPercentileCalculator;
import com.barraiser.onboarding.interview.pojo.InterviewData;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ScoreGenerator {

	private final InterViewRepository interViewRepository;
	private final EvaluationPercentileCalculator evaluationPercentileCalculator;
	private final EvaluationRepository evaluationRepository;
	private final ObjectMapper objectMapper;
	private final EvaluationScoreGenerator evaluationScoreGenerator;
	private final InterviewScoreGenerator interviewScoreGenerator;
	private final InterviewUtil interviewUtil;

	public void generateScores(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final List<InterviewData> interviews = this.getAllInterviewsToBeConsidered(interviewDAO);
		this.evaluationScoreGenerator.generateScores(interviews, interviewDAO.getEvaluationId());
		interviews.forEach(this.interviewScoreGenerator::generateScores);
		this.updateEvaluationPercentile(interviewDAO.getEvaluationId());
	}

	private List<InterviewData> getAllInterviewsToBeConsidered(final InterviewDAO interviewDAO) {
		final List<InterviewDAO> interviews = this.interViewRepository
				.findAllByEvaluationId(interviewDAO.getEvaluationId())
				.stream()
				.filter(x -> !x.getId().equals(interviewDAO.getId())
						&& InterviewStatus.fromString(x.getStatus()).isFeedbackSubmissionCompleted()
						&& !Boolean.FALSE.equals(this.interviewUtil.shouldInterviewBeConsideredForEvaluation(x)))
				.collect(Collectors.toList());
		interviews.add(interviewDAO);
		return interviews.stream().map(this::mapInterviewData)
				.collect(Collectors.toList());
	}

	private void updateEvaluationPercentile(final String evaluationId) {
		try {
			final Double percentile = this.evaluationPercentileCalculator
					.calculatePercentile(evaluationId);
			final EvaluationDAO evaluation = this.evaluationRepository
					.findById(evaluationId)
					.get();
			this.evaluationRepository.save(
					evaluation.toBuilder().percentile(percentile).build());
		} catch (final Exception e) {
			log.error(
					"could not calculate percentile for evaluation : "
							+ evaluationId,
					e);
		}
	}

	public void generateScoresForEvaluationId(final String evaluationId) {
		final List<InterviewDAO> interviews = this.fetchInterviewsForScoreGeneration(evaluationId);
		this.evaluationScoreGenerator
				.generateScores(interviews.stream().map(this::mapInterviewData)
						.collect(Collectors.toList()), evaluationId);
		this.updateEvaluationPercentile(evaluationId);
	}

	private List<InterviewDAO> fetchInterviewsForScoreGeneration(final String evaluationId) {
		return this.interViewRepository
				.findAllByEvaluationId(evaluationId)
				.stream()
				.filter(x -> InterviewStatus.fromString(x.getStatus()).isFeedbackSubmissionCompleted()
						&& !Boolean.FALSE.equals(this.interviewUtil.shouldInterviewBeConsideredForEvaluation(x)))
				.collect(Collectors.toList());
	}

	private InterviewData mapInterviewData(final InterviewDAO interviewDAO) {
		return this.objectMapper.convertValue(interviewDAO, InterviewData.class).toBuilder()
				.isSaasInterview(this.interviewUtil.isSaasInterview(interviewDAO.getInterviewRound()))
				.build();
	}
}
