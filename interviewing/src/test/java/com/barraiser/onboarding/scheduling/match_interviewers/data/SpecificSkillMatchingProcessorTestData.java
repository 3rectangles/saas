/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers.data;

import com.barraiser.onboarding.dal.ExpertSkillsDAO;
import com.barraiser.onboarding.dal.InterviewStructureSkillsDAO;
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
public class SpecificSkillMatchingProcessorTestData {

	private List<ExpertSkillsDAO> expertSkills;
	private List<InterviewerData> result;
	private List<InterviewStructureSkillsDAO> specificSkills;
	private List<InterviewerData> interviewers;
}
