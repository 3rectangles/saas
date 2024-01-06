/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers.data;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.WaitingReasonDAO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateEvaluationSearchHandlerTestData {
	private Map<String, InterviewDAO> interviews;
	private Map<String, EvaluationDAO> evaluations;
	private Map<String, PartnerCompanyDAO> partnerCompanies;
	private Map<String, WaitingReasonDAO> waitingReasons;
	private Map<String, JobRoleToInterviewStructureDAO> jobRoleToInterviewStructures;
}
