/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.Data;

@Data
public class ExpertReminderData {

	private String interviewId;
	private String timestampToWaitBeforeRemindingExpert;
	private InterviewDAO interviewDAO;
	private Integer durationBeforeInterviewInMinutesToSendReminder;
}
