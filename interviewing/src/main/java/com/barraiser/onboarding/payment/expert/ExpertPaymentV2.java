/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertPaymentV2 implements InterviewCostCalculator {

	private final InterviewUtil interviewUtil;

	private static final String VERSION = "FEEDBACK_TAT_BASED";
	private static final String STATUS = "Done";
	private static final Long hourToEpochMultiplier = 3600L;

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
		final Long interviewStartTime = data.getInterviewStartDate() == null ? interview.getStartDate()
				: data.getInterviewStartDate();
		final Long feedbackSubmissionTime = data.getFeedbackSubmissionTime() == null
				? interview.getExpertFeedbackSubmissionTime() != null
						? interview.getExpertFeedbackSubmissionTime()
						: interview.getFeedbackSubmissionTime()
				: data.getFeedbackSubmissionTime();

		if (feedbackSubmissionTime == null) {
			throw new IllegalArgumentException("Feedback is not submitted");
		}
		final Long timeTakenToSubmitFeedback = feedbackSubmissionTime - interviewStartTime;

		final Double multiplier = data.getExpert().getMultiplier() == null ? 1.0 : data.getExpert().getMultiplier();
		final InterviewStructureDAO interviewStructureDAO = this.interviewUtil
				.getInterviewStructureForInterview(interview);
		final Integer expectedExpertDurationInInterview = interviewStructureDAO.getDuration()
				- interviewStructureDAO.getExpertJoiningTime();

		if (timeTakenToSubmitFeedback <= 26 * hourToEpochMultiplier) {
			return ExpertPaymentUtil.calculateAmountPayable(data.getExpert().getBaseCost(), multiplier,
					expectedExpertDurationInInterview);

		} else if (timeTakenToSubmitFeedback <= 38 * hourToEpochMultiplier) {
			return ExpertPaymentUtil.calculateAmountPayable(data.getExpert().getBaseCost(), 1.0,
					expectedExpertDurationInInterview);

		} else {
			return 0.0;
		}
	}
}
