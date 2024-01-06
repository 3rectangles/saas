/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers.data;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetAllUnusedInterviewersTestData {

	private List<InterviewDAO> interviews;
	private List<InterviewerData> interviewers;
}
