/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertPaymentV1 implements InterviewCostCalculator {

	private final InterviewUtil interviewUtil;

	private static final String VERSION = "FLAT_PAYMENT";
	private static final String STATUS = "Done";

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
		final ExpertDAO expert = data.getExpert();
		final Double multiplier = expert.getMultiplier() == null ? 1.0 : expert.getMultiplier();
		final InterviewStructureDAO interviewStructureDAO = this.interviewUtil
				.getInterviewStructureForInterview(data.getInterview());
		final Integer expectedExpertDurationInInterview = interviewStructureDAO.getDuration()
				- interviewStructureDAO.getExpertJoiningTime();

		return ExpertPaymentUtil.calculateAmountPayable(expert.getBaseCost(), multiplier,
				expectedExpertDurationInInterview);
	}

}
