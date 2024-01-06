/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.recommendation.EvaluationRecommendationGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class BgsDataGenerator {

	private final ScoreGenerator scoreGenerator;
	private final EvaluationRecommendationGenerator evaluationRecommendationGenerator;
	private final InterViewRepository interViewRepository;

	public void generateBgsDataForInterview(final InterviewDAO interviewDAO) {
		if (this.shouldBGSDataBeGeneratedForInterview(interviewDAO)) {
			this.scoreGenerator.generateScores(interviewDAO.getId());
			// todo: (Tp-722) Recommendation should only be generated depending on
			// Saas/Isaas
			this.evaluationRecommendationGenerator
					.generateEvaluationRecommendationAndSaveToDatabase(interviewDAO, interviewDAO.getEvaluationId());
		}
	}

	public void generateBgsDataEvaluation(final String evaluationId) {
		if (this.shouldBGSDataBeGeneratedForEvaluation(evaluationId)) {
			this.scoreGenerator.generateScoresForEvaluationId(evaluationId);
			this.evaluationRecommendationGenerator
					.generateEvaluationRecommendationAndSaveToDatabase(evaluationId);
		}
	}

	public void generateBgsDataForInterview(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		this.generateBgsDataForInterview(interviewDAO);
	}

	private Boolean shouldBGSDataBeGeneratedForInterview(final InterviewDAO interviewDAO) {
		return !(RoundType.FASTTRACK.getValue().equals(interviewDAO.getInterviewRound()));
	}

	private Boolean shouldBGSDataBeGeneratedForEvaluation(final String evaluationId) {
		final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluationId);
		final List<InterviewDAO> fastTrackInterviews = interviews.stream()
				.filter(x -> RoundType.FASTTRACK.getValue().equals(x.getInterviewRound())).collect(Collectors.toList());
		if (fastTrackInterviews.size() > 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
