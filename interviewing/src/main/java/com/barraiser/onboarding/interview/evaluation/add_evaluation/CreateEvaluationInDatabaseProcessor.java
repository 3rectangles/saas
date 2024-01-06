/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.utilities.FormattingUtil;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.evaluation.DemoEvaluationChecker;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@AllArgsConstructor
@Component
public class CreateEvaluationInDatabaseProcessor implements AddEvaluationProcessor {
	private static final String EVALUATION_RECOMMENDATION_ALGO_VERSION = "evaluation_recommendation_algo_version";
	private static final String EVALUATION_STRATEGY_FOR_NEW_INTERIVEW_FLOW_VERSION = "14";

	private final EvaluationRepository evaluationRepository;
	private final StaticAppConfigValues staticAppConfigValues;
	private final EvaluationStatusManager evaluationStatusManager;
	private final DemoEvaluationChecker demoEvaluationChecker;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final FormattingUtil formattingUtil;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final InterviewStructureManager interviewStructureManager;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		this.createEvaluation(data);
	}

	public void createEvaluation(final AddEvaluationProcessingData data) {
		String targetCompanyId;
		if (data.getJobRoleDAO() == null) {
			targetCompanyId = this.partnerCompanyRepository.findById(data.getPartnerId()).get().getCompanyId();
		} else {
			targetCompanyId = data.getJobRoleDAO().getCompanyId();
		}

		if (data.getAuthenticatedUser() == null) {
			log.info("Injecting authenticated user to capture created by in evaluation.");
			data.setAuthenticatedUser(this.getEvaluationCreator(data.getPocEmail()));
		}

		final String evaluationId;
		if (data.getEvaluationId() != null
				&& data.getEvaluationId().length() > 0) {
			evaluationId = data.getEvaluationId();
		} else {
			evaluationId = UUID.randomUUID().toString();
		}

		String partnerId;
		if (data.getJobRoleDAO() == null) {
			partnerId = data.getPartnerId();

		} else {
			partnerId = data.getJobRoleDAO().getPartnerId();
		}

		final String createdByEmail = data.getAuthenticatedUser().getEmail();
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.id(evaluationId)
				.candidateId(data.getCandidateId())
				.companyId(targetCompanyId)
				.partnerId(partnerId)
				.jobRoleId(
						data.getJobRoleDAO() != null ? data.getJobRoleDAO().getEntityId().getId() : null)
				.jobRoleVersion(
						data.getJobRoleDAO() != null ? data.getJobRoleDAO().getEntityId().getVersion()
								: null)
				.pocEmail(data.getPocEmail())
				.defaultScoringAlgoVersion(
						data.getJobRoleDAO() != null ? this.getScoringAlgoVersion(data.getJobRoleDAO())
								: EVALUATION_STRATEGY_FOR_NEW_INTERIVEW_FLOW_VERSION)
				.defaultRecommendationVersion(this.dynamicAppConfigProperties
						.getString(EVALUATION_RECOMMENDATION_ALGO_VERSION))
				.source("B2B")
				.createdBy(createdByEmail)
				.isDemo(data.getJobRoleDAO() != null
						? this.demoEvaluationChecker.isDemoEvaluation(evaluationId,
								data.getJobRoleDAO().getEntityId().getId(),
								data.getJobRoleDAO().getEntityId().getVersion())
						: Boolean.FALSE)
				.build();
		this.evaluationRepository.save(evaluationDAO);

		final String transitionedBy = data.getAuthenticatedUser().getUserName();

		this.evaluationStatusManager.transitionBarRaiserStatus(evaluationDAO.getId(), "pending_assignment",
				transitionedBy);

		data.setEvaluationId(evaluationId);
		data.setResult(data.getResult().toBuilder().evaluationId(evaluationId).build());
	}

	private AuthenticatedUser getEvaluationCreator(final String pocEmails) {
		List<String> pocEmailsList = new ArrayList<>();
		if (pocEmails != null) {
			pocEmailsList = this.formattingUtil.convertStringToList(pocEmails, ",");
		}

		if (pocEmailsList.size() != 0) {
			final String evaluationCreatorEmail = pocEmailsList.get(0);

			return com.barraiser.commons.auth.AuthenticatedUser.builder()
					.userName(this.userInformationManagementHelper.findUserByEmail(evaluationCreatorEmail).get())
					.email(evaluationCreatorEmail)
					.build();
		}

		return null;
	}

	private String getScoringAlgoVersion(final JobRoleDAO jobRoleDAO) {
		if (this.interviewStructureManager.isInterviewStructureOnNewFlow(jobRoleDAO)) {
			return EVALUATION_STRATEGY_FOR_NEW_INTERIVEW_FLOW_VERSION;
		} else {
			return this.staticAppConfigValues.getCurrentEvaluationScoringAlgoVersion();
		}
	}
}
