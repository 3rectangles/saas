/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewRoundTypeConfigurationDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import lombok.Data;

import java.util.Map;

@Data
public class ExpertSchedulingCommunicationData {
	private Map<String, Object> communicationData;
	private Map<String, String> expertData;
	private InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO;
	private InterviewStructureDAO interviewStructureDAO;
	private InterviewDAO interviewDAO;
	private Long startDate;
	private String expertId;
}
