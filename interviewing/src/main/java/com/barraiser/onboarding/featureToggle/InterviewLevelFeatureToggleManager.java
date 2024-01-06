/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.featureToggle;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.partner.EvaluationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class InterviewLevelFeatureToggleManager {
	private final FeatureToggleManager featureToggleManager;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final EvaluationManager evaluationManager;

	public Map<String, Boolean> getFeatureToggles(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final Map<String, String> entity = new HashMap<>();
		entity.put("interviewer_id", interviewDAO.getInterviewerId());
		entity.put("round_type", interviewDAO.getInterviewRound());
		entity.put("partnerId", this.evaluationManager.getPartnerCompanyForEvaluation(evaluationDAO.getId()));
		return this.featureToggleManager
				.getFeatureToggleForEntity(interviewId, "INTERVIEW", entity);
	}

	public Boolean isFeatureOn(final String interviewId, final String featureToggleName) {
		return this.getFeatureToggles(interviewId).getOrDefault(featureToggleName, false);
	}
}
