/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.Data;

@Data
public class EvaluationServiceDeskProcessingData {
	private EvaluationDAO evaluationDAO;
	private String followUpDate;
}
