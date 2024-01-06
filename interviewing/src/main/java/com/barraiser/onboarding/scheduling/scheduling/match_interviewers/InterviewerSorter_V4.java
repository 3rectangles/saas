/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewerSorter_V4 implements InterviewerSorter {
	@Override
	public String version() {
		return "4";
	}

	@Override
	public List<InterviewerData> sort(final List<InterviewerData> interviewers, final Boolean isDemoEvaluation,
			final Boolean isFallbackConditionEnabled) {
		Collections.sort(
				interviewers,
				(InterviewerData i1, InterviewerData i2) -> {
					if (isDemoEvaluation) {
						return this.compareInterviewsForDemoEvaluation(i1, i2, isFallbackConditionEnabled);
					} else {
						return this.compareInterviewers(i1, i2, isFallbackConditionEnabled);
					}

				});
		return interviewers;
	}

	private Boolean isInterviewerEligibleForDemo(InterviewerData interviewer) {
		if (interviewer.getIsDemoEligible() == null) {
			return false;
		}
		return interviewer.getIsDemoEligible();
	}

	private int compareInterviewsForDemoEvaluation(final InterviewerData i1,
			final InterviewerData i2, final Boolean isFallbackEnabled) {
		if (this.isInterviewerEligibleForDemo(i1) && !this.isInterviewerEligibleForDemo(i2)) {
			return -1;
		} else if (this.isInterviewerEligibleForDemo(i2) && !this.isInterviewerEligibleForDemo(i1)) {
			return 1;
		} else {
			return this.compareInterviewers(i1, i2, isFallbackEnabled);
		}
	}

	private int compareInterviewers(final InterviewerData i1, final InterviewerData i2,
			final Boolean isFallbackEnabled) {
		final int comparisonBasedOnProficiency = i2.getAverageProficiencyInSkills()
				.compareTo(i1.getAverageProficiencyInSkills());
		if (comparisonBasedOnProficiency != 0) {
			return comparisonBasedOnProficiency;
		} else {
			return this.compareInterviewersIfProficiencyIsSame(i1, i2, isFallbackEnabled);
		}
	}

	private int compareInterviewersIfProficiencyIsSame(final InterviewerData i1, final InterviewerData i2,
			final Boolean isFallbackEnabled) {
		final int comparisonBasedOnSlotsBookedInAWeek = i1.getInterviewingSlotsBookedInAWeek()
				.compareTo(i2.getInterviewingSlotsBookedInAWeek());
		if (comparisonBasedOnSlotsBookedInAWeek != 0) {
			return comparisonBasedOnSlotsBookedInAWeek;
		} else {
			final int comparisonBasedOnSlotsBookedOnADay = Integer.compare(i1.getInterviewingSlotsBookedOnADay(),
					i2.getInterviewingSlotsBookedOnADay());
			if (comparisonBasedOnSlotsBookedOnADay != 0) {
				return comparisonBasedOnSlotsBookedOnADay;
			} else {
				if (Boolean.TRUE.equals(isFallbackEnabled)) {
					return i1.getMaxCostInINR().compareTo(i2.getMaxCostInINR());
				} else {
					return i2.getMaxCostInINR().compareTo(i1.getMaxCostInINR());
				}
			}
		}
	}
}
