/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewRoundTypeConfigurationDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SchedulingCommunicationData {
	private Map<String, Object> communicationData;
	private Map<String, String> candidateData;
	private Map<String, String> expertData;
	private Map<String, String> taData;
	private List<String> pocEmails;
	private InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO;
	private InterviewStructureDAO interviewStructureDAO;
	private InterviewDAO interviewDAO;
	private Boolean isCandidateAnonymous;
	private Long startDate;
	private String expertId;
}
