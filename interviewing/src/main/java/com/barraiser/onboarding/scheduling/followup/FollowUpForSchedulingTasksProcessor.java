/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.sfn.StepFunctionTaskProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class FollowUpForSchedulingTasksProcessor {
	private final GetFollowUpStatusAndSetWaitTime getFollowUpStatusAndSetWaitTime;
	private final AddExpiryMessageForPartner addExpiryMessageForPartner;
	private final GetIvrResponseStatus getIvrResponseStatus;
	private final CallCandidateManuallyForScheduling callCandidateManuallyForScheduling;
	private final UpdateFollowUpTimeOnJira updateFollowUpTimeOnJira;
	private final StepFunctionTaskProcessor<FollowUpForSchedulingStepFunctionDTO> stepFunctionTaskProcessor;

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void getFollowUpStatusAndSetWaitTime() throws Exception {
		this.stepFunctionTaskProcessor.handle(FollowUpConstants.GET_FOLLOW_STATUS_AND_SET_WAIT_TIME,
				this.getFollowUpStatusAndSetWaitTime, FollowUpForSchedulingStepFunctionDTO.class);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void addExpiryMessage() throws Exception {
		this.stepFunctionTaskProcessor.handle(FollowUpConstants.ADD_EXPIRY_MESSAGE_FOR_PARTNER,
				this.addExpiryMessageForPartner, FollowUpForSchedulingStepFunctionDTO.class);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void getIvrResponseStatus() throws Exception {
		this.stepFunctionTaskProcessor.handle(FollowUpConstants.GET_IVR_RESPONSE_STATUS, this.getIvrResponseStatus,
				FollowUpForSchedulingStepFunctionDTO.class);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void callCandidateManually() throws Exception {
		this.stepFunctionTaskProcessor.handle(FollowUpConstants.CALL_CANDIDATE_MANUALLY,
				this.callCandidateManuallyForScheduling, FollowUpForSchedulingStepFunctionDTO.class);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void updateFollowUpDateAndTime() throws Exception {
		this.stepFunctionTaskProcessor.handle(FollowUpConstants.UPDATE_FOLLOW_UP_DATE_ON_JIRA,
				this.updateFollowUpTimeOnJira, FollowUpForSchedulingStepFunctionDTO.class);
	}

}
