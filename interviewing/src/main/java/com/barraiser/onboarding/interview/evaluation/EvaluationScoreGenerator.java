/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import com.barraiser.onboarding.analytics.BGSScoreDataAnalyticsPopulator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import static com.barraiser.onboarding.common.Constants.ROUND_TYPE_INTERNAL;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationScoreGenerator {
	public static final String ENTITY_TYPE_EVALUATION = "evaluation";

	private final EvaluationRepository evaluationRepository;
	private final List<EvaluationStrategy> evaluationStrategyList;
	private final BGSScoreDataAnalyticsPopulator BGSScoreDataAnalyticsPopulator;
	private final EvaluationGenerationRequestToDTOConverter evaluationGenerationRequestPreProcessor;
	private final EvaluationScoreService evaluationScoreService;
	private final EmailService emailService;

	@Transactional
	public void generateScores(final List<InterviewData> interviews, final String evaluationId) {
		final EvaluationDAO evaluationDAO = evaluationRepository.findById(evaluationId).get();
		this.evaluationStrategyList.forEach(
				evaluationStrategy -> {
					if (evaluationDAO.getDefaultScoringAlgoVersion().equals(evaluationStrategy.version())) {
						try {
							this.generateAndSaveScoresForBarRaiserProcess(
									interviews, evaluationStrategy, evaluationId);
							this.generateAndSaveScoresForInternalCompanyProcess(interviews, evaluationStrategy,
									evaluationId);
							this.generateAndSaveScoresForOverallProcess(interviews, evaluationStrategy, evaluationId);
						} catch (final ObjectOptimisticLockingFailureException exception) {
							throw exception;
						} catch (final Exception exception) {
							log.info(
									"Exception occurred while generating score for evaluation : {} , exception : {} ",
									evaluationId, exception, exception);
							this.sendEvaluationGenerationFailureEmail(
									evaluationId, evaluationStrategy, exception);
						}
					}
				});
	}

	private void generateAndSaveScoresForBarRaiserProcess(final List<InterviewData> interviews,
			final EvaluationStrategy evaluationStrategy, final String evaluationId) {
		final List<InterviewData> interviewsToBeConsidered = interviews.stream()
				.filter(x -> !ROUND_TYPE_INTERNAL.equalsIgnoreCase(x.getInterviewRound()))
				.collect(Collectors.toList());
		this.generateAndSaveScores(interviewsToBeConsidered, evaluationStrategy, InterviewProcessType.BARRAISER,
				evaluationId);
	}

	private void generateAndSaveScoresForInternalCompanyProcess(final List<InterviewData> interviews,
			final EvaluationStrategy evaluationStrategy, final String evaluationId) {
		final List<InterviewData> interviewRoundsToBeConsidered = interviews.stream()
				.filter(x -> ROUND_TYPE_INTERNAL.equalsIgnoreCase(x.getInterviewRound()))
				.collect(Collectors.toList());
		this.generateAndSaveScores(interviewRoundsToBeConsidered, evaluationStrategy,
				InterviewProcessType.PARTNER_INTERNAL, evaluationId);
	}

	private final void generateAndSaveScoresForOverallProcess(
			final List<InterviewData> interviews, final EvaluationStrategy evaluationStrategy,
			final String evaluationId) {
		this.generateAndSaveScores(interviews, evaluationStrategy, InterviewProcessType.OVERALL, evaluationId);
	}

	@SneakyThrows
	private void sendEvaluationGenerationFailureEmail(
			final String evaluationId,
			final EvaluationStrategy evaluationStrategy,
			final Exception exception) {

		// We don't want email to fail, sometimes, these values are null, to handle that
		// case, they
		// are appended to an
		// empty String.
		final Map<String, String> data = Map.of(
				"evaluationId", "" + evaluationId,
				"errorMessage", "" + exception.getMessage(),
				"algoVersion", "" + evaluationStrategy.version());
		this.emailService.sendEmail(
				"monitoring@barraiser.com",
				"Evaluation Generation Failed",
				"evaluation_generation_failure",
				data,
				null);
	}

	private void generateAndSaveScores(final List<InterviewData> interviews,
			final EvaluationStrategy evaluationStrategy,
			final InterviewProcessType interviewProcessType, final String evaluationId) {
		final ComputeEvaluationScoresData computeScoreInputDTO = this.evaluationGenerationRequestPreProcessor
				.process(interviews, evaluationId);
		final EvaluationScoreData evaluationScoreData = evaluationStrategy.computeEvaluationScore(computeScoreInputDTO);
		this.evaluationScoreService.save(
				evaluationId,
				evaluationStrategy.version(),
				interviewProcessType,
				evaluationScoreData.getSkillScores());

		this.BGSScoreDataAnalyticsPopulator.saveAnalyticsForBGSCalculation(evaluationId,
				ENTITY_TYPE_EVALUATION,
				evaluationStrategy.version(),
				evaluationScoreData.getInput(),
				interviewProcessType);
	}
}
