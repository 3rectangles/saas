/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.barraiser.onboarding.common.Constants.*;

@Log4j2
@Component
@AllArgsConstructor
public class CancellationPaymentCalculatorV1 implements InterviewCostCalculator {

	private static final String VERSION = "V1";
	private static final String STATUS = "cancellation_done";

	private static final Long minuteToEpochMultiplier = 60L;
	private static final Integer minutesInAnHour = 60;
	private final InterviewUtil interviewUtil;
	private final CancellationReasonManager cancellationReasonManager;

	private static final Double paymentPercentageIfCancelledBeforeStartTime = 12.5 / 100;
	private static final Double paymentPercentageIfCancelledInInitialMinutesAfterStart = 25.0 / 100;

	public Double interviewCancelledBeforeStartTime(final ExpertDAO expert, Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (((multiplier * expert.getBaseCost())) * (Double.valueOf(interviewDurationInMinutes) / minutesInAnHour)
				* paymentPercentageIfCancelledBeforeStartTime);
	}

	public Double interviewCancelledWithinInitialMinutesAfterStart(final ExpertDAO expert,
			Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (((multiplier * expert.getBaseCost())) * (Double.valueOf(interviewDurationInMinutes) / minutesInAnHour)
				* paymentPercentageIfCancelledInInitialMinutesAfterStart);
	}

	public Double interviewCancelledInLaterMinutesAfterStart(final ExpertDAO expert,
			Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (multiplier * expert.getBaseCost()) * (Double.valueOf(interviewDurationInMinutes) / minutesInAnHour);
	}

	@Override
	public String status() {
		return STATUS;
	}

	@Override
	public String version() {
		return VERSION;
	}

	@Override
	public Double calculate(final InterviewPaymentCalculationData data) {
		final InterviewDAO interview = data.getInterview();
		;
		final InterviewStructureDAO interviewStructureDAO = this.interviewUtil
				.getInterviewStructureForInterview(interview);
		final Integer expertJoiningTime = interviewStructureDAO.getExpertJoiningTime();
		final Long interviewStartTime = data.getInterviewStartDate() == null ? interview.getStartDate()
				: data.getInterviewStartDate();
		final Long startTimeOfInterviewForExpert = interviewStartTime + (expertJoiningTime * minuteToEpochMultiplier);
		final Long cancellationTimeOfInterview = data.getCancellationTime();
		final String cancellationReason = data.getCancellationReasonId();
		final Integer interviewDurationInMinutes = interviewStructureDAO.getDuration();

		/**
		 * Not cancelled by expert is carefully chosen.
		 * As there can be several other categories of reason in the
		 * future other than cancelled by candidate.
		 * In all those cases the expert payment should come under consideration.
		 */
		if (!this.isCancelledByExpert(cancellationReason)) {
			if (startTimeOfInterviewForExpert > cancellationTimeOfInterview
					&& (startTimeOfInterviewForExpert - cancellationTimeOfInterview) <= 30
							* minuteToEpochMultiplier) {
				return this.interviewCancelledBeforeStartTime(data.getExpert(), interviewDurationInMinutes);

			} else if (cancellationTimeOfInterview >= startTimeOfInterviewForExpert
					&& (cancellationTimeOfInterview - startTimeOfInterviewForExpert) <= 20
							* minuteToEpochMultiplier) {
				return this.interviewCancelledWithinInitialMinutesAfterStart(data.getExpert(),
						interviewDurationInMinutes);

			} else if (cancellationTimeOfInterview >= startTimeOfInterviewForExpert
					&& (cancellationTimeOfInterview - startTimeOfInterviewForExpert) > 20
							* minuteToEpochMultiplier) {
				return this.interviewCancelledInLaterMinutesAfterStart(data.getExpert(), interviewDurationInMinutes);
			}
		}

		return 0.0;
	}

	private Boolean isCancelledByExpert(final String cancellationReasonId) {
		final List<String> expertCancellationReasonIds = this.cancellationReasonManager
				.getCancellationReasonsByUserTypeAndProcessType(
						List.of(USER_TYPE_EXPERT, CANCELLATION_TYPE_CANDIDATE_AND_EXPERT), PROCESS_TYPE_INTERVIEW);
		return expertCancellationReasonIds.contains(cancellationReasonId);
	}
}
