/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import java.util.List;

public interface InterviewerSorter {

	String version();

	List<InterviewerData> sort(List<InterviewerData> interviewers, Boolean isDemoEvaluation,
			Boolean isFallbackConditionEnabled);
}
