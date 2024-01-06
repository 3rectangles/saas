/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import lombok.Data;

import java.util.List;

@Data
public class InterviewersPerDayData {

	private String date;

	private List<InterviewerData> interviewers;
}
