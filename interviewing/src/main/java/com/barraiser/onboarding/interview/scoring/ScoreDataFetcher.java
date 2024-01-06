/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.scoring;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.Score;
import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.barraiser.onboarding.interview.evaluation.scores.ScoreScaleConverter;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreDAO;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.dataloader.DataLoader;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Log4j2
public class ScoreDataFetcher implements MultiParentTypeDataFetcher {

	private final InterviewScoreRepository interviewScoreRepository;
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final ScoreScaleConverter scoreScaleConverter;
	public static final String INTERVIEW_LEVEL_SCORE_DATA_LOADER = "INTERVIEW_LEVEL_SCORE_DATA_LOADER";
	private static final String TYPE_INTERVIEW = "Interview";

	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(TYPE_INTERVIEW, "score"));
	}

	public DataLoader<Pair<String, String>, Score> createInterviewScoreDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<Pair<String, String>> interviewIdAlgoSet) -> CompletableFuture.supplyAsync(
						() -> this.getScoreForInterviews(interviewIdAlgoSet), executor));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

		if (TYPE_INTERVIEW.equalsIgnoreCase(type.getName())) {
			final DataLoader<Pair<String, String>, List<SkillScore>> interviewLevelSkillScoreDataLoader = environment
					.getDataLoader(INTERVIEW_LEVEL_SCORE_DATA_LOADER);
			final Interview interview = environment.getSource();
			return interviewLevelSkillScoreDataLoader.load(
					Pair.of(interview.getId(), interview.getScoringAlgoVersion()));
		} else {
			throw new IllegalArgumentException(
					"Bad parent type while accessing skill score type, please fix your query");
		}
	}

	private Map<Pair<String, String>, Score> getScoreForInterviews(
			final Set<Pair<String, String>> interviewIdAlgoSet) {
		final Map<Pair<String, String>, Score> interviewToScoreMap = new HashMap<>();

		final List<String> interviewIds = interviewIdAlgoSet.stream()
				.map(Pair::getFirst)
				.distinct()
				.collect(Collectors.toList());

		final List<InterviewScoreDAO> skillLevelScoresForInterview = this.interviewScoreRepository
				.findAllByInterviewIdIn(interviewIds);

		// Keep only those scores that are applicable for the algorithm version
		// no scale bgs calvulator used as skill scores are already scaled
		for (final Pair<String, String> interviewIdAlgo : interviewIdAlgoSet) {
			final List<SkillScore> scoresForInterviewAndAlgoCombination = this.getAllSkillLevelScores(
					skillLevelScoresForInterview,
					interviewIdAlgo.getFirst(),
					interviewIdAlgo.getSecond());
			final Score score = Score.builder()
					.skillScores(scoresForInterviewAndAlgoCombination)
					.bgs(BgsCalculator.calculateBgsNoScaleDouble(scoresForInterviewAndAlgoCombination))
					.build();
			interviewToScoreMap.put(interviewIdAlgo, score);
		}
		return interviewToScoreMap;
	}

	private List<SkillScore> getAllSkillLevelScores(
			final List<InterviewScoreDAO> interviewScores,
			final String interviewId,
			final String defaultScoringAlgo) {
		InterviewDAO interviewDAO = interViewRepository.findById(interviewId).get();
		return interviewScores.stream()
				.filter(
						is -> (interviewId.equals(is.getInterviewId())
								&& defaultScoringAlgo.equals(is.getScoringAlgoVersion())))
				.map(interviewScoreDAO -> toSkillScoreScale(interviewScoreDAO, interviewDAO.getPartnerId()))
				.collect(Collectors.toList());
	}

	// todo: 722 get according to config
	private SkillScore toSkillScoreScale(final InterviewScoreDAO interviewScoreDAO, String partnerId) {
		return SkillScore.builder()
				.skillId(interviewScoreDAO.getSkillId())
				.weightage(interviewScoreDAO.getWeightage())
				.score(scoreScaleConverter.convertScoreFrom800(interviewScoreDAO.getScore(), partnerId))
				.build();
	}
}
