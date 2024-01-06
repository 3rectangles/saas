/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.barraiser.onboarding.common.Constants.*;

/**
 * Policy document :
 * https://docs.google.com/document/d/1aUHQAcBXdrp9_Ho0qmKSY1kSbYFvuLBC9SsvT4i_cFE/edit
 */

@Log4j2
@Component
@AllArgsConstructor
public class CancellationPaymentCalculatorV2 implements InterviewCostCalculator {

	private static final String VERSION = "2";
	private static final String STATUS = "cancellation_done";

	private static final Long minuteToEpochMultiplier = 60L;
	private static final Double MINUTES_IN_AN_HOUR = 60.0;

	private final CancellationReasonManager cancellationReasonManager;

	private static final Double paymentPercentageIfCancelledBeforeStartTime = 12.5 / 100;
	private static final Double paymentPercentageIfCancelledInInitialMinutesAfterStart = 25.0 / 100;

	public Double calculatePaymentForInterviewCancelledBeforeStartTime(final ExpertDAO expert,
			Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (((multiplier * expert.getBaseCost()))
				* this.getHoursToBeConsideredForPayment(interviewDurationInMinutes)
				* paymentPercentageIfCancelledBeforeStartTime);
	}

	public Double calculateForinterviewCancelledWithinInitialMinutesAfterStart(final ExpertDAO expert,
			Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (((multiplier * expert.getBaseCost()))
				* this.getHoursToBeConsideredForPayment(interviewDurationInMinutes)
				* paymentPercentageIfCancelledInInitialMinutesAfterStart);
	}

	public Double calculateForInterviewCancelledInLaterMinutesAfterStart(final ExpertDAO expert,
			Integer interviewDurationInMinutes) {
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		return (multiplier * expert.getBaseCost()) * this.getHoursToBeConsideredForPayment(interviewDurationInMinutes);
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
		final InterviewStructureDAO interviewStructureDAO = data.getInterviewStructure();
		final Integer expertJoiningTime = interviewStructureDAO.getExpertJoiningTime();
		final Long interviewStartTime = data.getInterviewStartDate() == null ? interview.getStartDate()
				: data.getInterviewStartDate();
		final Long interviewStartTimeForExpert = interviewStartTime + (expertJoiningTime * minuteToEpochMultiplier);
		final Long cancellationTimeOfInterview = data.getCancellationTime();
		final String cancellationReason = data.getCancellationReasonId();
		final Integer interviewDuration = data.getInterviewStructure().getDuration();

		/**
		 * We do not have a cancellation policy for interviews between 30 and 1 hour
		 * durations
		 */
		if (interviewDuration > 30 && interviewDuration < 60) {
			throw new IllegalArgumentException(
					"We do not have a cancellation policy in place for interviews between 30 mins and 60 mins of duration");
		}

		/**
		 * Not cancelled by expert is carefully chosen.
		 * As there can be several other categories of reason in the
		 * future other than cancelled by candidate.
		 * In all those cases the expert payment should come under consideration.
		 */
		if (!this.isCancelledByExpert(cancellationReason)) {
			return this.isInterviewCancelledBeforeStartTime(interviewStartTimeForExpert, cancellationTimeOfInterview) ?

					this.calculatePaymentForInterviewCancelledBeforeStartTime(interviewStartTimeForExpert,
							cancellationTimeOfInterview, data)
					:

					this.calculatePaymentForInterviewCancelledAfterStartTime(interviewStartTimeForExpert,
							cancellationTimeOfInterview, data);
		}

		return 0.0;
	}

	private Double calculatePaymentForInterviewCancelledBeforeStartTime(final Long interviewStartTimeForExpert,
			final Long cancellationTimeOfInterview, final InterviewPaymentCalculationData paymentCalculationData) {
		final Integer interviewDurationInMinutes = paymentCalculationData.getInterviewStructure().getDuration();
		final Integer paymentDecisionTimeForInterviewCancelledBeforeStart = this
				.getPaymentDecisionTimeForInterviewCancelledBeforeStartTime(interviewDurationInMinutes);

		if ((interviewStartTimeForExpert
				- cancellationTimeOfInterview) <= paymentDecisionTimeForInterviewCancelledBeforeStart
						* minuteToEpochMultiplier) {
			return this.calculatePaymentForInterviewCancelledBeforeStartTime(paymentCalculationData.getExpert(),
					interviewDurationInMinutes);
		}
		return 0.0;
	}

	private Double calculatePaymentForInterviewCancelledAfterStartTime(final Long interviewStartTimeForExpert,
			final Long cancellationTimeOfInterview, final InterviewPaymentCalculationData paymentCalculationData) {
		final Integer interviewDurationInMinutes = paymentCalculationData.getInterviewStructure().getDuration();
		final Integer paymentDecisionTimeForInterviewCancelledAfterStart = this
				.getPaymentDecisionTimeForInterviewCancelledAfterStartTime(interviewDurationInMinutes);

		if (this.isInterviewCancelledWithinInitialMinutesAfterStart(interviewStartTimeForExpert,
				cancellationTimeOfInterview, paymentDecisionTimeForInterviewCancelledAfterStart)) {
			return this.calculateForinterviewCancelledWithinInitialMinutesAfterStart(paymentCalculationData.getExpert(),
					interviewDurationInMinutes);

		} else if (this.isInterviewCancelledInLaterMinutesAfterStart(interviewStartTimeForExpert,
				cancellationTimeOfInterview, paymentDecisionTimeForInterviewCancelledAfterStart)) {
			return this.calculateForInterviewCancelledInLaterMinutesAfterStart(paymentCalculationData.getExpert(),
					interviewDurationInMinutes);
		}

		return 0.0;
	}

	/**
	 * @param interviewDurationInMinutes
	 * @return This function returns a time in minutes
	 *         Ex: Return value : 30 mins
	 *         If interview is cancelled before 30 mins BEFORE the interview start
	 *         vs cancelled inside of 30 mins BEFORE the interview start
	 *         the payment calculation will differ.
	 */
	private Integer getPaymentDecisionTimeForInterviewCancelledBeforeStartTime(
			final Integer interviewDurationInMinutes) {
		return interviewDurationInMinutes <= 30 ? 30 : 30;
	}

	/**
	 * @param interviewDurationInMinutes
	 * @return This function returns a time in minutes
	 *         Ex: Return value : 30 mins
	 *         If interview is cancelled before 30 mins AFTER the interview start
	 *         vs cancelled inside of 30 mins AFTER the interview start
	 *         the payment calculation will differ.
	 */
	private Integer getPaymentDecisionTimeForInterviewCancelledAfterStartTime(
			final Integer interviewDurationInMinutes) {
		return interviewDurationInMinutes <= 30 ? 15 : 20;
	}

	private Double getHoursToBeConsideredForPayment(final Integer interviewDurationInMinutes) {
		return (interviewDurationInMinutes <= 30 ? 30 : 60) / MINUTES_IN_AN_HOUR;
	}

	private Boolean isCancelledByExpert(final String cancellationReasonId) {
		final List<String> expertCancellationReasonIds = this.cancellationReasonManager
				.getCancellationReasonsByUserTypeAndProcessType(
						List.of(USER_TYPE_EXPERT, CANCELLATION_TYPE_CANDIDATE_AND_EXPERT), PROCESS_TYPE_INTERVIEW);
		return expertCancellationReasonIds.contains(cancellationReasonId);
	}

	private Boolean isInterviewCancelledBeforeStartTime(final Long interviewStartTimeForExpert,
			final Long cancellationTimeOfInterview) {
		return interviewStartTimeForExpert > cancellationTimeOfInterview;
	}

	private Boolean isInterviewCancelledWithinInitialMinutesAfterStart(final Long interviewStartTimeForExpert,
			final Long cancellationTimeOfInterview, final Integer paymentDecisionTimeForInterviewCancelledAfterStart) {
		return (cancellationTimeOfInterview >= interviewStartTimeForExpert
				&& (cancellationTimeOfInterview
						- interviewStartTimeForExpert) <= paymentDecisionTimeForInterviewCancelledAfterStart
								* minuteToEpochMultiplier);
	}

	private Boolean isInterviewCancelledInLaterMinutesAfterStart(final Long interviewStartTimeForExpert,
			final Long cancellationTimeOfInterview, final Integer paymentDecisionTimeForInterviewCancelledAfterStart) {
		return (cancellationTimeOfInterview >= interviewStartTimeForExpert
				&& (cancellationTimeOfInterview
						- interviewStartTimeForExpert) > paymentDecisionTimeForInterviewCancelledAfterStart
								* minuteToEpochMultiplier);
	}
}
