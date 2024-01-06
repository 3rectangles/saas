/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class InterviewLifecycleUtil {

	public static final String DYNAMO_SFN_ENABLED_INTERIVEWS = "sfn-interview-lifecycle-enabled-interview";
	public static final String DYNAMO_SFN_ENABLED_TA_ALLOCATION = "sfn-interview-lifecycle-enabled-ta-allocation";
	private final JobRoleManager jobRoleManager;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final EmailService emailService;
	private final StaticAppConfigValues staticAppConfigValues;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;

	public Boolean checkIfStepFunctionIsEnabledForInterview(final String interviewId) {

		if (Boolean.parseBoolean(this.staticAppConfigValues.getInterviewLifecycleManagementEnabled())) {
			// NOTE : Added this toggle to allow us to push to prod and try out for some
			// interviews or all interviews
			// change flag value to 'all', to enable it for all interviews
			final boolean interviewLifecycleSfnEnabledForInterview = this.dynamicAppConfigProperties
					.getString(DYNAMO_SFN_ENABLED_INTERIVEWS).equalsIgnoreCase("all") ||
					Arrays.asList(this.dynamicAppConfigProperties.getString(DYNAMO_SFN_ENABLED_INTERIVEWS).split(","))
							.contains(interviewId);

			return interviewLifecycleSfnEnabledForInterview;
		}
		return false;
	}

	public Boolean checkIfStepFunctionIsEnabledForTaAllocation(final String interviewId) {

		if (Boolean.parseBoolean(this.staticAppConfigValues.getTaAutoAllocationEnabled())) {
			final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
			log.info("Fetched interview :{}", interviewDAO);
			final EvaluationDAO evaluation = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
			final String companyId = this.jobRoleManager.getJobRoleFromEvaluation(evaluation).get().getCompanyId();
			final boolean isTaAutoAllocationEnabled = this.dynamicAppConfigProperties
					.getString(DYNAMO_SFN_ENABLED_TA_ALLOCATION).equalsIgnoreCase("all") ||
					Arrays.asList(
							this.dynamicAppConfigProperties.getString(DYNAMO_SFN_ENABLED_TA_ALLOCATION).split(","))
							.contains(interviewId)
					||
					Arrays.asList(
							this.dynamicAppConfigProperties.getString(DYNAMO_SFN_ENABLED_TA_ALLOCATION).split(","))
							.contains(companyId);
			return isTaAutoAllocationEnabled;
		}
		return false;
	}

	public void informOperations(final String interviewId, final String subject, final String errors)
			throws IOException {
		final String toEmail = "operations@barraiser.com";

		final Map<String, String> data = new HashMap<>();
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(interviewId)
				.orElse(JiraUUIDDAO.builder().build());
		data.put("jira", jiraUUIDDAO.getJira());
		data.put("errors", errors);
		this.emailService.sendEmail(toEmail, subject, "operations_interview_lifecycle_error", data, null);
	}

}
