/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.featureToggle.FeatureToggleManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.DemoEvaluationChecker;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class OverBookingThresholdCalculator {
	public static final String OVERBOOKING_THRESHOLD_PERCENTAGE = "overbooking-threshold";
	public static final String OVERBOOKING_THRESHOLD_PERCENTAGE_TA = "overbooking-threshold-ta";
	public static final String OVERBOOKING_FEATURE_TOGGLE = "overbooking";
	private final DynamicAppConfigProperties appConfigProperties;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final FeatureToggleManager featureToggleManager;
	private final DemoEvaluationChecker demoEvaluationChecker;

	public double getOverBookingThresholdForExpert(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		final boolean isOverbookingFeatureEnabled = this.isOverbookingOfExpertsEnabled(evaluationDAO.getId(),
				interviewId, jobRoleDAO);
		if (Boolean.FALSE.equals(isOverbookingFeatureEnabled)) {
			return 0;
		} else {
			return (double) this.appConfigProperties.getInt(OVERBOOKING_THRESHOLD_PERCENTAGE) / (double) 100;
		}
	}

	public double getOverBookingThresholdForTa() {
		return (double) this.appConfigProperties.getInt(OVERBOOKING_THRESHOLD_PERCENTAGE_TA) / (double) 100;
	}

	private boolean isOverbookingOfExpertsEnabled(final String evaluationId, final String interviewId,
			final JobRoleDAO jobRoleDAO) {
		final Map<String, String> entity = Map.of(
				"demo",
				"" + this.demoEvaluationChecker.isDemoEvaluation(evaluationId, jobRoleDAO.getEntityId().getId(),
						jobRoleDAO.getEntityId().getVersion()),
				"companyId", jobRoleDAO.getCompanyId());
		final Map<String, Boolean> featureToggleForEntity = this.featureToggleManager
				.getFeatureToggleForEntity(interviewId, "INTERVIEW", entity);
		return featureToggleForEntity.getOrDefault(OVERBOOKING_FEATURE_TOGGLE, true);
	}
}
