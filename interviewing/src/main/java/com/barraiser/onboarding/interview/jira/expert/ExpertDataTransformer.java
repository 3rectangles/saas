/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class ExpertDataTransformer {

	public List<InterviewerData> toInterviewerData(final List<ExpertSearchDAO> expertSearchDAOS,
			final Integer workExperienceOfIntervieweeInMonths) {
		return expertSearchDAOS.stream().filter(this::isExpertDataValid).map(data -> InterviewerData.builder()
				.id(data.getUserId())
				.workExperienceInMonthsRelative(
						data.getWorkExperienceInMonths()
								- workExperienceOfIntervieweeInMonths)
				.baseCost(data.getCostPerHour())
				.multiplier(data.getMultiplier())
				.currencyCode(data.getCurrency())
				.isDemoEligible(data.getIsDemoEligible())
				.minCostPerHour(data.getMinCostPerHour())
				.build()).collect(Collectors.toList());
	}

	private boolean isExpertDataValid(final ExpertSearchDAO data) {
		return data.getCostPerHour() != null
				&& data.getMultiplier() != null
				&& data.getCurrency() != null && data.getMinCostPerHour() != null;
	}

}
