/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CancellationPaymentCalculatorV1Test {
	@Mock
	private InterviewUtil interviewUtil;

	@Mock
	private CancellationReasonManager cancellationReasonManager;

	@InjectMocks
	private CancellationPaymentCalculatorV1 cancellationPaymentCalculatorV1;

	private static InterviewDAO interview;

	private static InterviewDAO interview30MinsDuration;

	private static ExpertDAO expert;

	private static final Long interviewStartTime = 1609439400l;

	@Before
	public void setup() {
		expert = ExpertDAO.builder()
				.id("expert_id")
				.baseCost(1500.0)
				.multiplier(1.5)
				.build();

		interview = InterviewDAO.builder()
				.id("interview_id")
				.interviewerId("expert_id")
				.status(InterviewStatus.DONE.getValue())
				.startDate(interviewStartTime)
				.interviewRound("PEER")
				.build();

		interview30MinsDuration = InterviewDAO.builder()
				.id("interview_id_30")
				.interviewerId("expert_id")
				.status(InterviewStatus.DONE.getValue())
				.startDate(interviewStartTime)
				.interviewRound("PEER")
				.cancellationReasonId("random_id")
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(any())).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(60)
						.expertJoiningTime(0)
						.build());

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any())).thenReturn(
				List.of("expert_cancellation_reason_1",
						"expert_cancellation_reason_2",
						"expert_cancellation_reason_3"));

	}

	@Test
	public void testPaymentCalculationWhenExpertJoiningDifferentFromInterviewStart() {

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 55 * 60))
				.cancellationReasonId("candidate_cancellation_reason")
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(any())).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(120)
						.expertJoiningTime(75)
						.build());
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime + 55 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(562.5, this.cancellationPaymentCalculatorV1.calculate(data));
	}

	@Test
	public void testPaymentCalculationWhenInterviewCancelledByExpert() {
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 30 * 60))
				.cancellationReasonId("expert_cancellation_reason_1")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("1")
				.cancellationTime(interviewStartTime + 30 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertTrue(this.cancellationPaymentCalculatorV1.calculate(data) == 0.0);
	}

	@Test
	public void testPaymentShouldBeNonZeroWhenInterviewNotCancelledByExpertAfterInterviewStart() {
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 30 * 60))
				.cancellationReasonId("random_cancellation_reason_1")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime + 30 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertFalse(this.cancellationPaymentCalculatorV1.calculate(data) == 0.0);
	}

	@Test
	public void testCalculateExpertPaymentForCancellationWithinEarlierThan30MinsBeforeStartTime() {
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (100 * 60)))
				.cancellationReasonId("random_id")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime - (100 * 60))
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(0, this.cancellationPaymentCalculatorV1.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentForCancellationWithin30MinsBeforeStartTime() {
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (20 * 60)))
				.cancellationReasonId("random_id")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime - (20 * 60))
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(281.25, this.cancellationPaymentCalculatorV1.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentForCancellationWithin20MinsAfterStart() {

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 5 * 60))
				.cancellationReasonId("random_id")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime + 5 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(562.5, this.cancellationPaymentCalculatorV1.calculate(data));
	}

	@Test
	public void expertPaymentForCancellationInLaterMinutesAfterStartTest() {
		interview30MinsDuration = interview30MinsDuration.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 15 * 60))
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(interview30MinsDuration)).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(30)
						.expertJoiningTime(0)
						.build());

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview30MinsDuration)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime + 15 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(281.25, this.cancellationPaymentCalculatorV1.calculate(data));

	}

	@Test
	public void expertPaymentForCancellationInInitialMinutesAfterStartTest() {
		interview30MinsDuration = interview30MinsDuration.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + 5 * 60))
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(interview30MinsDuration)).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(30)
						.expertJoiningTime(0)
						.build());

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview30MinsDuration)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime + 5 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(281.25, this.cancellationPaymentCalculatorV1.calculate(data));
	}

	@Test
	public void expertPaymentForCancellationBeforeStartTest() {
		interview30MinsDuration = interview30MinsDuration.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - 15 * 60))
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(interview30MinsDuration)).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(30)
						.expertJoiningTime(0)
						.build());

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview30MinsDuration)
				.expert(expert)
				.cancellationReasonId("2")
				.cancellationTime(interviewStartTime - 15 * 60)
				.build();

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any()))
				.thenReturn(List.of("1"));

		assertEquals(140.625, this.cancellationPaymentCalculatorV1.calculate(data));
	}

}
