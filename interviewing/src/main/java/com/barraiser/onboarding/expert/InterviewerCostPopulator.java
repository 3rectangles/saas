/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewerCostPopulator {
	public static final Long minutesInAnHour = 60L;
	private final CostUtil costUtil;

	public List<InterviewerData> populateTotalCostInINR(List<InterviewerData> interviewers,
			final Long durationOfInterview, final Integer expertJoiningTime) {
		interviewers = interviewers.stream()
				.map(
						x -> x.toBuilder()
								.maxCostInINR(
										this.getTotalCostInINR(
												this.getCostOfExpertForInterview(x.getBaseCost(), x.getMultiplier(),
														durationOfInterview, expertJoiningTime),
												x.getCurrencyCode()))
								.minCostInINR(this.getTotalCostInINR(
										this.getCostOfExpertForInterview(x.getMinCostPerHour(), 1D,
												durationOfInterview, expertJoiningTime),
										x.getCurrencyCode()))
								.build())
				.collect(Collectors.toList());
		return interviewers;
	}

	private Double getTotalCostInINR(
			final Double costOfExpert, final String currencyCode) {
		return this.costUtil.convertToINR(costOfExpert, currencyCode);
	}

	private Double getCostOfExpertForInterview(final Double baseCostOfExpert, final Double multiplier,
			final Long durationOfInterview, final Integer expertJoiningTime) {
		final Double expertJoiningTimeInMinutes = (double) expertJoiningTime / 60;
		return baseCostOfExpert * multiplier * ((durationOfInterview - expertJoiningTimeInMinutes) / minutesInAnHour);
	}
}
