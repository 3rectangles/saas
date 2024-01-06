/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.scheduling.sfn.ExpertAllocationToInterviewProcessor;
import com.barraiser.onboarding.scheduling.scheduling.sfn.TriggerInterviewingLifecycleActivity;
import com.barraiser.onboarding.scheduling.scheduling.sfn.UpdateInterviewInJiraActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This is a relaxed/lean implementation of our much complex scheduling
 * mechanism
 * used in iaas.
 * <p>
 * Steps involved
 * - Trigger the bot
 * - Assign interviewer and interviewee
 * - Update Interview details on Jira and in DB
 */
@Log4j2
@AllArgsConstructor
@Component
public class SaasInterviewSchedulingService {

	@Qualifier("SaasSchedulingDataValidationProcessor")
	private final SaasDataValidationProcessor dataValidationProcessor;
	private final UpdateInterviewProcessor updateInterviewProcessor;
	private final UpdateInterviewInJiraActivity updateInterviewInJiraActivity;
	private final TriggerInterviewingLifecycleActivity triggerInterviewingLifecycleActivity;
	private final InterviewService interviewService;
	private final ObjectMapper objectMapper;

	public void scheduleInterview(final AuthenticatedUser user, final ScheduleInterviewInput input)
			throws Exception {
		final SchedulingProcessingData data = new SchedulingProcessingData();
		data.setUser(user);
		data.setInput(input);
		data.setSchedulingPlatform(input.getSchedulingPlatform());
		data.setIsExpertDuplicate(Boolean.FALSE);

		// 1.Validate data to see if scheduling is possible
		this.dataValidationProcessor.process(data);

		// 2. This step is important to assign the meeting link
		this.updateInterviewProcessor.process(data);

		// 2. Allocate expert to interview.
		final InterviewDAO interviewDAO = this.interviewService.findById(data.getInput().getInterviewId());
		this.interviewService.save(
				interviewDAO.toBuilder().interviewerId(data.getInput().getInterviewerId())
						.build(),
				data.getInput().getSchedulingPlatform(), data.getInput().getSchedulingPlatform());

		this.triggerInterviewingLifecycleActivity.process(this.objectMapper.writeValueAsString(data));

		// Confirm if this books the meetting and assigns the interviewer and
		// interviewee
		// Not sure if this is needed.
		// this.updateInterviewInDbActivity.process();

		this.updateInterviewInJiraActivity.process(this.objectMapper.writeValueAsString(data));

	}
}
