/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.RuleDAO;
import com.barraiser.onboarding.dal.RuleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewRoundClearanceRuleChecker {

	private final RuleEngineOrchestrator ruleEngineOrchestrator;
	private final RuleRepository ruleRepository;

	private final JSONParser parser = new JSONParser();

	public boolean doesInterviewMeetsRule(
			final InterviewDAO interview,
			final String approvalCriteria)
			throws ParseException {
		final RuleProcessingData ruleProcessingData = RuleProcessingData.builder()
				.entityId(interview.getId())
				.entityType("INTERVIEW")
				.ruleType("SELECTION_CRITERIA")
				.ruleBody((JSONObject) parser.parse(approvalCriteria))
				.build();
		return this.ruleEngineOrchestrator.process(ruleProcessingData);
	}

	public String getRuleFromRuleId(String approvalRuleId) {
		final RuleDAO ruleDAO = this.ruleRepository.findById(approvalRuleId).orElse(null);
		return ruleDAO != null ? ruleDAO.getRuleBody() : null;
	}

	public boolean isInterviewApproved(
			JobRoleToInterviewStructureDAO currentJobRoleToInterviewStructureDAO,
			InterviewDAO interviewDAO) {

		if (currentJobRoleToInterviewStructureDAO.getApprovalRuleId() == null) {
			return false;
		}
		final String approvalCriteria = getRuleFromRuleId(
				currentJobRoleToInterviewStructureDAO.getApprovalRuleId());
		try {
			if (doesInterviewMeetsRule(interviewDAO, approvalCriteria)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error while parsing rule");

		}
		return false;
	}

	public boolean isInterviewRejected(
			JobRoleToInterviewStructureDAO currentJobRoleToInterviewStructureDAO,
			InterviewDAO interviewDAO) {
		if (currentJobRoleToInterviewStructureDAO.getRejectionRuleId() == null) {
			return false;
		}
		final String rejectionCriteria = getRuleFromRuleId(
				currentJobRoleToInterviewStructureDAO.getRejectionRuleId());
		try {
			if (doesInterviewMeetsRule(
					interviewDAO, rejectionCriteria)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}
}
