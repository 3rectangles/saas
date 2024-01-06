/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.ta;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.cancellation.CancellationProcessingData;
import com.barraiser.onboarding.scheduling.cancellation.CancellationProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@AllArgsConstructor
@Component
class AssignNewInterviewToTaProcessor implements CancellationProcessor {
	private final InterViewRepository interViewRepository;
	private final AvailabilityManager availabilityManager;
	private final InterviewService interviewService;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		if (!data.getIsTaAutoAllocationEnabled() || Objects.isNull(data.getInterviewForTaReassignment()))
			return;

		final InterviewDAO interviewDAO = data.getInterviewForTaReassignment();
		data.setInterviewForTaReassignment(interViewRepository.findById(interviewDAO.getId()).get());

		if (Objects.nonNull(data.getInterviewForTaReassignment().getTaggingAgent())) {
			data.setIsTaAutoAllocationEnabled(false);
			return;
		}
		this.availabilityManager.bookSlotNeedlessAvailability(
				data.getPreviousStateOfCancelledInterview().getTaggingAgent(),
				"BarRaiser", interviewDAO.getStartDate(), interviewDAO.getEndDate(),
				availabilityManager.getBufferForTa());

		this.changeTaForInterview(data.getInterviewForTaReassignment(),
				data.getPreviousStateOfCancelledInterview().getTaggingAgent(), data);

	}

	private void changeTaForInterview(InterviewDAO interviewDAO, final String taggingAgent,
			CancellationProcessingData data) {
		interviewDAO = interviewDAO.toBuilder().status(InterviewStatus.PENDING_INTERVIEWING.getValue())
				.taggingAgent(taggingAgent).build();
		interviewDAO = this.interviewService.save(interviewDAO);
		data.setInterviewForTaReassignment(interviewDAO);
	}
}
