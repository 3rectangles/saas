/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class SearchNewInterviewTaProcessor implements CancellationProcessor {
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;
	private final DynamicAppConfigProperties appConfigProperties;
	public static final String DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC = "time-to-wait-before-informing-ops";

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		if (!data.getIsTaAutoAllocationEnabled())
			return;
		final InterviewDAO interviewCancelledByCandidate = data.getPreviousStateOfCancelledInterview();
		final InterviewDAO interviewDAO = this.getInterviewScheduledWithInThatSlot(
				interviewCancelledByCandidate.getStartDate(),
				interviewCancelledByCandidate.getEndDate(), this.getStatusToBeSkippedForTaAllocation());
		data.setInterviewForTaReassignment(interviewDAO);
	}

	private InterviewDAO getInterviewScheduledWithInThatSlot(final Long startDate, final Long endDate,
			List<String> statusToBeSkipped) {
		final Long effectiveStartDate = Math.max(
				(System.currentTimeMillis() / 1000L)
						+ (long) this.appConfigProperties.getInt(DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC),
				startDate);
		final InterviewDAO interviewDAO = this.interViewRepository
				.findTopByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotInAndTaggingAgentIsNullAndIsTaggingAgentNeeded(
						this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent(), effectiveStartDate, endDate,
						statusToBeSkipped, Boolean.TRUE);
		return interviewDAO;
	}

	private List<String> getStatusToBeSkippedForTaAllocation() {
		return Arrays.asList(InterviewStatus.CANCELLATION_DONE.getValue(), InterviewStatus.DONE.getValue(),
				InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue());
	}
}
