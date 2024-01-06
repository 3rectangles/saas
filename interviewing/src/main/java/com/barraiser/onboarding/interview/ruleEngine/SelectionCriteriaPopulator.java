/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreDAO;
import com.barraiser.onboarding.interview.scoring.dal.InterviewScoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class SelectionCriteriaPopulator implements RulePopulator {

	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final BgsScoreFetcher bgsScoreFetcher;
	private final EvaluationRepository evaluationRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewScoreRepository interviewScoreRepository;
	private final ObjectMapper objectMapper;

	private final String ROUND_LEVEL_SCORE = "ROUND_LEVEL_SCORE";
	private final String SKILL_LEVEL_SCORE = "SKILL_LEVEL_SCORE";

	@Override
	public Object populate(Map<String, Object> valuesMap) {

		final String interviewId = (String) valuesMap.get("entityId");
		final JSONArray entities = (JSONArray) valuesMap.get("entityPath");
		final String type = (String) valuesMap.get("type");
		switch (type) {
			case ROUND_LEVEL_SCORE:
				return this.getRoundLevelScore(
						interviewId, (Long) ((JSONObject) entities.get(0)).get("value"));
			case SKILL_LEVEL_SCORE:
				return this.getSkillLevelScore(
						interviewId,
						(Long) ((JSONObject) entities.get(0)).get("value"),
						(String) ((JSONObject) entities.get(1)).get("value"));
			default:
				return 0;
		}
	}

	public InterviewDAO getInterviewForRound(final String interviewId, final Long roundIndex) {
		final EvaluationDAO evaluationDAO = this.getEvaluationForInterview(interviewId);
		final Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndOrderIndex(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion(),
						roundIndex.intValue());
		final InterviewDAO interviewDAO = this.getInterviewToBeConsideredForEvaluationAndInterviewStructure(
				evaluationDAO.getId(),
				jobRoleToInterviewStructureDAO.get().getInterviewStructureId());
		return interviewDAO;
	}

	public Object getRoundLevelScore(final String interviewId, final Long roundIndex) {
		final InterviewDAO interviewDAO = this.getInterviewForRound(interviewId, roundIndex);
		final int bgsScore = this.bgsScoreFetcher.getBgsScoreForInterview(interviewDAO.getId());
		return (long) bgsScore;
	}

	public Object getSkillLevelScore(
			final String interviewId, final Long roundIndex, final String skillId) {
		final InterviewDAO interviewDAO = this.getInterviewForRound(interviewId, roundIndex);
		final EvaluationDAO evaluationDAO = this.getEvaluationForInterview(interviewDAO.getId());

		final List<SkillScore> skillScores = this.getSkillScoresForInterview(interviewDAO.getId(), evaluationDAO);
		final Optional<SkillScore> skillScore = skillScores.stream()
				.filter(score -> score.getSkillId().equals(skillId))
				.findFirst();
		if (skillScore.isPresent()) {
			return (long) skillScore.get().getScore().intValue();
		} else
			return (long) 800;
	}

	private List<SkillScore> getSkillScoresForInterview(
			final String interviewId, final EvaluationDAO evaluation) {
		final List<InterviewScoreDAO> interviewScores = this.interviewScoreRepository
				.findAllByInterviewIdAndScoringAlgoVersion(interviewId, evaluation.getDefaultScoringAlgoVersion());
		return interviewScores.stream().map(x -> this.objectMapper.convertValue(x, SkillScore.class))
				.collect(Collectors.toList());
	}

	public EvaluationDAO getEvaluationForInterview(final String interviewId) {
		return this.evaluationRepository
				.findById(this.interViewRepository.findById(interviewId).get().getEvaluationId())
				.get();
	}

	public InterviewDAO getInterviewToBeConsideredForEvaluationAndInterviewStructure(final String evaluationId,
			final String interviewStructureId) {
		final List<InterviewDAO> interviews = this.interViewRepository.findByEvaluationIdAndInterviewStructureId(
				evaluationId, interviewStructureId);
		return interviews.stream().filter(x -> x.getRedoReasonId() != null).findFirst().get();
	}

	@Override
	public String ruleType() {
		return "SELECTION_CRITERIA";
	}
}
