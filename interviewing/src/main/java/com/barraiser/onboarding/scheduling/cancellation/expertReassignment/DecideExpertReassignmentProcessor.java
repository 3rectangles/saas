/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.common.utilities.DateUtils;

import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.config.ExpertReassignmentConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class DecideExpertReassignmentProcessor implements ExpertReassignmentProcessor {
	public static Long TWELVE_HOURS_IN_SECONDS = 12 * 60 * 60L;
	private final ExpertReassignmentConfig config;

	private final DateUtils dateUtils;

	private boolean shouldExpertBeReassigned(final ExpertReassignmentData data) {
		final boolean wasCancelledMidnight = this.dateUtils.isBetweenTimeOfDay(
				data.getCancellationRequestedTimeOfInterview(),
				this.config.getCancellationTimeUpperBound(),
				this.config.getCancellationTimeLowerBound(), DateUtils.TIMEZONE_ASIA_KOLKATA);
		final boolean wasScheduledForMidnight = this.dateUtils.isBetweenTimeOfDay(
				data.getInterview().getStartDate(),
				this.config.getScheduledTimeUpperBound(),
				this.config.getScheduledTimeLowerBound(), DateUtils.TIMEZONE_ASIA_KOLKATA);
		final boolean wasCancelledOnSameDayOfInterview = data.getInterview().getStartDate()
				- data.getCancellationRequestedTimeOfInterview() <= TWELVE_HOURS_IN_SECONDS;

		return !((wasCancelledMidnight && wasScheduledForMidnight)
				&& wasCancelledOnSameDayOfInterview);
	}

	private boolean shouldExpertBeReassignedBySystem(
			final Long scheduledTimeOfOriginalInterview,
			final Long cancellationTimeOfOriginalInterview) {
		return scheduledTimeOfOriginalInterview - cancellationTimeOfOriginalInterview >= this.config
				.getMinTimeForSystemToReassignExpert();
	}

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		final Boolean shouldExpertBeReassignedForInterview = this.shouldExpertBeReassigned(data);
		data.setShouldExpertBeReassigned(shouldExpertBeReassignedForInterview);

		final Boolean shouldExpertBeReassignedForInterviewBySystem = shouldExpertBeReassignedForInterview
				&& this.shouldExpertBeReassignedBySystem(
						data.getInterview().getStartDate(),
						data.getCancellationRequestedTimeOfInterview());
		data.setShouldExpertBeReassignedBySystem(shouldExpertBeReassignedForInterviewBySystem);

		data.setTimestampToWaitBeforeSendingRescheduledMail(
				this.dateUtils.getFormattedDateString(
						data.getInterview().getStartDate()
								- this.config.getMaxDurationToAllowExpertReassignment(),
						DateUtils.TIMEZONE_UTC,
						DateUtils.DATEFORMAT_ISO_8601));
	}
}
