/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;

import java.util.List;

public class ExpertPaymentUtil {
	/**
	 * @param expectedExpertDurationInInterview
	 *            duration in seconds for which the expert
	 *            is expected to join the interview.
	 * @return Time in minutes for which the expert is eligible to be paid
	 */
	public static Integer getPayableDurationInMinutes(final Integer expectedExpertDurationInInterview) {

		final Integer payableDurationForExpert = (expectedExpertDurationInInterview > 0
				&& expectedExpertDurationInInterview <= 30) ? 30
						: (expectedExpertDurationInInterview > 30 && expectedExpertDurationInInterview <= 60) ? 60
								: expectedExpertDurationInInterview;

		return payableDurationForExpert;
	}

	/**
	 * @param basePrice
	 *            Base cost of the expert
	 * @param multiplier
	 *            Multiplier is decided based on different scoring strategy.
	 * @param expectedExpertDurationInInterview
	 *            Duration in seconds for which the
	 *            expert is expected to join the interview.
	 * @return
	 */
	public static Double calculateAmountPayable(final Double basePrice, final Double multiplier,
			final Integer expectedExpertDurationInInterview) {
		return (Double.valueOf(getPayableDurationInMinutes(expectedExpertDurationInInterview))
				/ DateUtils.MINUTES_IN_ONE_HOUR) * (multiplier * basePrice);
	}

	public static Double getAverageCostOfInterviewers(final List<InterviewerData> interviewers) {
		final Double minCostOfInterviewers = interviewers.stream()
				.map(InterviewerData::getMaxCostInINR)
				.mapToDouble(Double::doubleValue)
				.min()
				.orElse(0D);
		final Double maxCostOfInterviewers = interviewers.stream()
				.map(InterviewerData::getMaxCostInINR)
				.mapToDouble(Double::doubleValue)
				.max()
				.orElse(0D);
		return (minCostOfInterviewers + maxCostOfInterviewers) / 2;
	}

}
