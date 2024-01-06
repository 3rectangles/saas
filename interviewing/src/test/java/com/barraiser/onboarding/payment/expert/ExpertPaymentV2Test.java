/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExpertPaymentV2Test {

	@Mock
	private InterviewUtil interviewUtil;

	@InjectMocks
	private ExpertPaymentV2 expertPaymentV2;

	private static ExpertDAO expert;
	private static InterviewDAO interview;

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
				.interviewRound("PEER")
				.build();

		when(this.interviewUtil.getInterviewStructureForInterview(any())).thenReturn(
				InterviewStructureDAO.builder()
						.id("test_structure")
						.duration(60)
						.expertJoiningTime(0)
						.build());
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionBefore26Hours() {
		interview = interview.toBuilder()
				.feedbackSubmissionTime(interviewStartTime + 10 * 3600)
				.interviewerId("expert_id")
				.status(InterviewStatus.DONE.getValue())
				.startDate(interviewStartTime)
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.build();

		assertEquals(2250.0, this.expertPaymentV2.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionBetween26And38Hours() {
		interview = interview.toBuilder()
				.feedbackSubmissionTime(interviewStartTime + 30 * 3600)
				.interviewerId("expert_id")
				.status(InterviewStatus.DONE.getValue())
				.startDate(interviewStartTime)
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.build();
		assertEquals(1500.0, this.expertPaymentV2.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionPost38Hours() {
		interview = interview.toBuilder()
				.feedbackSubmissionTime(interviewStartTime + 40 * 3600)
				.interviewerId("expert_id")
				.status(InterviewStatus.DONE.getValue())
				.startDate(interviewStartTime)
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.build();

		assertEquals(0.0, this.expertPaymentV2.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionPost38HoursGivenInEvent() {
		interview = interview.toBuilder()
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStartDate(interviewStartTime)
				.feedbackSubmissionTime(interviewStartTime + 40 * 3600)
				.build();

		assertEquals(0.0, this.expertPaymentV2.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionBetween26And38HoursGivenInEvent() {
		interview = interview.toBuilder()
				.interviewerId("expert_id")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStartDate(interviewStartTime)
				.feedbackSubmissionTime(interviewStartTime + 30 * 3600)
				.build();
		assertEquals(1500.0, this.expertPaymentV2.calculate(data));
	}

	@Test
	public void testCalculateExpertPaymentFeedbackSubmissionBefore26HoursGivenInEvent() {
		interview = interview.toBuilder()
				.interviewerId("expert_id")
				.build();
		final InterviewPaymentCalculationData data = InterviewPaymentCalculationData.builder()
				.interview(interview)
				.expert(expert)
				.interviewStartDate(interviewStartTime)
				.feedbackSubmissionTime(interviewStartTime + 10 * 3600)
				.build();

		assertEquals(2250.0, this.expertPaymentV2.calculate(data));
	}

}
