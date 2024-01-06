/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewRoundTypeConfigurationDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import lombok.Data;

import java.util.Map;

@Data
public class SchedulingEmailData {
	private Map<String, Object> emailData;
	private Map<String, String> candidateData;
	private Map<String, String> expertData;
	private String pocEmail;
	private InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO;
	private InterviewStructureDAO interviewStructureDAO;
	private InterviewDAO interviewDAO;
}
