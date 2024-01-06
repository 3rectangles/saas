/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.payment.expert.ExpertPaymentUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewerSorter_V3 implements InterviewerSorter {

	@Override
	public String version() {
		return "3";
	}

	@Override
	public List<InterviewerData> sort(final List<InterviewerData> interviewers, final Boolean isDemoEvaluation,
			final Boolean isFallbackConditionEnabled) {
		final Double averageCostOfAllInterviewers = ExpertPaymentUtil.getAverageCostOfInterviewers(interviewers);
		this.performSorting(interviewers, averageCostOfAllInterviewers, isDemoEvaluation);
		return interviewers;
	}

	private Boolean isInterviewerEligibleForDemo(InterviewerData interviewer) {
		if (interviewer.getIsDemoEligible() == null) {
			return false;
		}
		return interviewer.getIsDemoEligible();
	}

	private void performSorting(List<InterviewerData> interviewers, Double averageCostOfAllInterviewers,
			Boolean isDemoEvaluation) {
		Collections.sort(
				interviewers,
				(InterviewerData i1, InterviewerData i2) -> {

					if (isDemoEvaluation) {
						return this.compareInterviewsForDemoEvaluation(averageCostOfAllInterviewers, i1, i2);
					} else {
						return this.compareInterviews(averageCostOfAllInterviewers, i1, i2);
					}

				});
	}

	private int compareInterviewsForDemoEvaluation(Double averageCostOfAllInterviewers, InterviewerData i1,
			InterviewerData i2) {
		if (this.isInterviewerEligibleForDemo(i1) && !this.isInterviewerEligibleForDemo(i2)) {
			return -1;
		} else if (this.isInterviewerEligibleForDemo(i2) && !this.isInterviewerEligibleForDemo(i1)) {
			return 1;
		} else {
			return this.compareInterviews(averageCostOfAllInterviewers, i1, i2);
		}
	}

	private int compareInterviews(Double averageCostOfAllInterviewers, InterviewerData i1, InterviewerData i2) {
		if (i1.getInterviewingSlotsBookedInAWeek() == 0 && i2.getInterviewingSlotsBookedInAWeek() > 0) {
			return -1;
		} else if (i2.getInterviewingSlotsBookedInAWeek() == 0 && i1.getInterviewingSlotsBookedInAWeek() > 0) {
			return 1;
		} else {
			if (i1.getAverageProficiencyInSkills() - i2.getAverageProficiencyInSkills() == 0) {
				return this.compareBasedOnAverageCost(averageCostOfAllInterviewers, i1, i2);
			} else {
				if (i1.getAverageProficiencyInSkills() < i2.getAverageProficiencyInSkills())
					return 1;
				else if (i1.getAverageProficiencyInSkills() > i2.getAverageProficiencyInSkills())
					return -1;
				else
					return 0;
			}
		}
	}

	private int compareBasedOnAverageCost(
			final Double averageCost, final InterviewerData i1, final InterviewerData i2) {
		if (averageCost >= i1.getMaxCostInINR() && averageCost < i2.getMaxCostInINR()) {
			if (Math.floor(i1.getInterviewingSlotsBookedOnADay() * 0.5) > i2.getInterviewingSlotsBookedOnADay()) {
				return 1;
			} else {
				return -1;
			}
		} else if (averageCost < i1.getMaxCostInINR() && averageCost >= i2.getMaxCostInINR()) {
			if (Math.floor(i2.getInterviewingSlotsBookedOnADay() * 0.5) > i1.getInterviewingSlotsBookedOnADay()) {
				return -1;
			} else {
				return 1;
			}
		} else if (averageCost >= i1.getMaxCostInINR() && averageCost >= i2.getMaxCostInINR()) {
			return this.compareWhenBothLessThanAverageCost(i1, i2);
		} else {
			return this.compareWhenBothGreaterThanAverageCost(i1, i2);
		}
	}

	private int compareWhenBothLessThanAverageCost(
			final InterviewerData i1, final InterviewerData i2) {
		if (Math.floor(i1.getInterviewingSlotsBookedOnADay() * 0.5) > Math
				.floor(i2.getInterviewingSlotsBookedOnADay() * 0.5)) {
			return 1;
		} else if (Math.floor(i1.getInterviewingSlotsBookedOnADay() * 0.5) < Math
				.floor(i2.getInterviewingSlotsBookedOnADay() * 0.5)) {
			return -1;
		} else {
			if (i1.getMaxCostInINR() < i2.getMaxCostInINR())
				return -1;
			else if (i1.getMaxCostInINR() > i2.getMaxCostInINR())
				return 1;
			else
				return 0;
		}
	}

	private int compareWhenBothGreaterThanAverageCost(
			final InterviewerData i1, final InterviewerData i2) {
		if (i1.getInterviewingSlotsBookedOnADay() > i2.getInterviewingSlotsBookedOnADay()) {
			return 1;
		} else if (i1.getInterviewingSlotsBookedOnADay() < i2.getInterviewingSlotsBookedOnADay()) {
			return -1;
		} else {
			if (i1.getMaxCostInINR() < i2.getMaxCostInINR())
				return -1;
			else if (i1.getMaxCostInINR() > i2.getMaxCostInINR())
				return 1;
			else
				return 0;
		}
	}
}
