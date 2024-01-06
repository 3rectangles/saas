/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationScoreFetcher;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//todo: tp-722 Check where it is used
@Component
@Log4j2
@AllArgsConstructor
public class GreenhouseTestStatusRequestHandler {
	private final ErrorCommunication errorCommunication;
	private final EvaluationScoreFetcher evaluationScoreFetcher;
	private final BgsCalculator bgsCalculator;

	public GreenhouseTestStatus getGreenhouseTestStatus(final EvaluationDAO evaluationDAO)
			throws Exception {

		final String scoringAlgo = evaluationDAO.getDefaultScoringAlgoVersion();

		final Map<String, Map<String, List<SkillScore>>> processTypeToEvaluationScoresMap = this.evaluationScoreFetcher
				.getAllScores(List.of(evaluationDAO), scoringAlgo);

		final List<SkillScore> overallProcessScores = processTypeToEvaluationScoresMap
				.get(InterviewProcessType.OVERALL.getValue())
				.get(evaluationDAO.getId());

		final double partnerScore = bgsCalculator.calculateBgs(overallProcessScores, evaluationDAO.getPartnerId());

		String bgsEvaluationUrl = null;
		if (evaluationDAO.getStatus().equals(EvaluationStatus.DONE.getValue())) {
			bgsEvaluationUrl = String.format(
					"https://app.barraiser.com/candidate-evaluation/%s",
					evaluationDAO.getId());
		}

		final GreenhouseTestStatus testStatus;
		try {
			testStatus = GreenhouseTestStatus.builder()
					.partnerStatus(evaluationDAO.getStatus().equals(EvaluationStatus.DONE.getValue()) ? "complete"
							: evaluationDAO.getStatus())
					.partnerProfileUrl(bgsEvaluationUrl)
					.partnerScore(partnerScore)
					.build();
		} catch (final Exception e) {
			this.errorCommunication.sendFailureEmailToTech("Greenhouse send test failed: ", e);
			log.info("error: " + e.getMessage());
			throw e;
		}

		return testStatus;
	}
}
