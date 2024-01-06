/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.graphql.types.Slot;
import com.barraiser.onboarding.dal.ExpertSkillsDAO;

import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewerData {
	private String id;
	private String currentCompanyName;
	private List<String> lastCompanyNames;
	private Double maxCostInINR;
	private List<Slot> availability;
	private Integer workExperienceInMonths;
	private int slotsBookedOnADay;
	private int interviewingSlotsBookedOnADay;
	private Integer workExperienceInMonthsRelative;
	private Double averageProficiencyInSkills;
	private List<ExpertSkillsDAO> specificSkills;
	private String duplicatedFrom;
	private Integer remainingNumberOfInterviews;
	private Integer slotsBookedInAWeek;
	private Integer interviewingSlotsBookedInAWeek;
	private Boolean isDemoEligible;
	private String currencyCode;
	private Double multiplier;
	private Double baseCost;
	private Double minCostInINR;
	private Double minCostPerHour;
}
