/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.CancellationReasonManager;
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
public class CancellationPaymentCalculatorV2Test {
	@Mock
	private CancellationReasonManager cancellationReasonManager;

	@InjectMocks
	private CancellationPaymentCalculatorV2 cancellationPaymentCalculatorV2;

	private static InterviewDAO interview;

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

		when(this.cancellationReasonManager.getCancellationReasonsByUserTypeAndProcessType(any(), any())).thenReturn(
				List.of("expert_cancellation_reason_1",
						"expert_cancellation_reason_2",
						"expert_cancellation_reason_3"));

	}

	// ------------------------- Tests for Interview of 30 mins duration
	// ----------------------------

	@Test
	public void testCalculation30MinInterviewScenario1() {

		/**
		 * Case :
		 * Interview duration = 30 mins &
		 * Expert Joining at start of interview &
		 * cancellation before a time BEFORE 30 mins from interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (100 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(30)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime - (100 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(0.0, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculation30MinInterviewScenario2() {
		/**
		 * Case :
		 * Interview duration = 30 mins &
		 * Expert Joining at start of interview &
		 * cancellation time WITHIN 30 mins from interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (20 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(30)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime - (20 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(140.625, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculation30MinInterviewScenario3() {
		/**
		 * Case :
		 * Interview duration = 30 mins &
		 * Expert Joining at start of interview &
		 * cancellation time WITHIN 15 mins after interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (10 * 60)))
				.cancellationReasonId("random_id")
				.build();

		InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(30)
				.expertJoiningTime(0)
				.build();

		InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (10 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(281.25, this.cancellationPaymentCalculatorV2.calculate(data));

	}

	@Test
	public void testCalculation30MinInterviewScenario4() {

		/**
		 * Case :
		 * Interview duration = 30 mins &
		 * Expert Joining at start of interview &
		 * cancellation time lies beyond 15 mins AFTER interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (25 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(30)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (25 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(1125, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	// ------------------------- Tests for Interviews > 30 mins duration
	// ----------------------------

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario1() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining at start of interview &
		 * cancellation time lies BEFORE 30 mins from interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (100 * 60)))
				.cancellationReasonId("random_id")
				.build();

		InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(0)
				.build();

		InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime - (100 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(0.0, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario2() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining at start of interview &
		 * cancellation time lies WITHIN 30 mins from interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime - (20 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime - (20 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(281.25, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario3() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining at start of interview &
		 * cancellation time lies WITHIN 20 mins after interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (10 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (10 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(562.50, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario4() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining at start of interview &
		 * cancellation time lies AFTER 20 mins after interview start
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (25 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (25 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(2250, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario5() {
		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining time after start of interview &
		 * cancellation time > interview start time &
		 * cancellation time lies before a time BEFORE 30 mins from expert joining time
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (10 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(45)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (10 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(0.0, this.cancellationPaymentCalculatorV2.calculate(data));

	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario6() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining time after start of interview &
		 * cancellation time > interview start time &
		 * cancellation time lies before a time WITHIN 30 mins from expert joining time
		 */
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (20 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(45)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (20 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(281.25, this.cancellationPaymentCalculatorV2.calculate(data));

	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario7() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining time after start of interview &
		 * cancellation time WITHIN 20 mins from expert joining time
		 */

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (55 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(45)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (55 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(562.50, this.cancellationPaymentCalculatorV2.calculate(data));

	}

	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario8() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining time after start of interview &
		 * cancellation time AFTER 20 mins from expert joining time
		 */
		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (85 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(45)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (85 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(2250, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	@Test(expected = IllegalArgumentException.class)
	public void interviewDurationGreaterThan30MinsAndLessThan60Mins() {

		interview = interview.toBuilder()
				.cancellationTime(String.valueOf(interviewStartTime + (85 * 60)))
				.cancellationReasonId("random_id")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(45)
				.expertJoiningTime(0)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (85 * 60))
				.cancellationReasonId("random_id")
				.build();

		assertEquals(2250, this.cancellationPaymentCalculatorV2.calculate(data));
	}

	/*
	 * Case :
	 * Interview duration >= 90 mins &
	 * Expert Joining time after start of interview &
	 * cancellation time AFTER 85 mins from expert joining time
	 * and start time of interview sent in the event
	 **/
	@Test
	public void testCalculationGreaterThanEqualTo60MinInterviewScenario9() {

		/**
		 * Case :
		 * Interview duration >= 60 mins &
		 * Expert Joining time after start of interview &
		 * cancellation time AFTER 20 mins from expert joining time
		 */
		InterviewDAO actualInterview = InterviewDAO.builder()
				.id("interview_id")
				.interviewRound("PEER")
				.build();

		final InterviewStructureDAO interviewStructure = InterviewStructureDAO.builder()
				.id("test_structure")
				.duration(90)
				.expertJoiningTime(45)
				.build();

		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(actualInterview)
				.expert(expert)
				.interviewStructure(interviewStructure)
				.cancellationTime(interviewStartTime + (85 * 60))
				.cancellationReasonId("random_id")
				.interviewStartDate(1609439400L)
				.build();

		assertEquals(2250, this.cancellationPaymentCalculatorV2.calculate(data));
	}

}
