/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.InterviewSchedulingCommunicationService;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.scheduling.sfn.ExpertAllocationToInterviewProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class InterviewSchedulingService {

	private final DataValidationProcessor dataValidationProcessor;
	private final InterviewSchedulingStepFunctionProcessor interviewSchedulingStepFunctionProcessor;
	private final UpdateInterviewProcessor updateInterviewProcessor;
	private final InterviewSchedulingCommunicationService interviewSchedulingCommunicationService;
	private final StaticAppConfigValues staticAppConfigValues;
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;
	private final ExpertAllocationToInterviewProcessor expertAllocationToInterviewProcessor;

	public InterviewDAO scheduleInterview(final AuthenticatedUser user, final ScheduleInterviewInput input)
			throws Exception {
		final SchedulingProcessingData data = new SchedulingProcessingData();
		data.setUser(user);
		data.setInput(input);
		data.setSchedulingPlatform(input.getSchedulingPlatform());

		// 1.Validate data to see if scheduling is possible
		this.dataValidationProcessor.process(data);

		// 2. Allocate expert to interview. (This involves assigning the expert to
		// interview,
		// sending communications to expert, calculating cost that will be given to
		// expert upon completion of interview,
		// handling in case of duplicate expert)
		this.expertAllocationToInterviewProcessor.process(data);

		// 3. Update Status of interview in DB
		this.updateInterviewProcessor.process(data);

		// 4. prepare email data
		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		data.setSchedulingCommunicationData(
				this.interviewSchedulingCommunicationService.prepareInterviewScheduledCommunicationData(interviewDAO));

		// 5.Ta Flow execution Flag
		data.setIsTAAutoAllocationNeeded(this.isTAAutoAllocationNeeded(interviewDAO));
		data.setExecuteTaAssignment(Boolean.parseBoolean(this.staticAppConfigValues.getTaAutoAllocationEnabled()));

		// 6. start execution of step function
		this.interviewSchedulingStepFunctionProcessor.process(data);

		return interviewDAO;
	}

	private Boolean isTAAutoAllocationNeeded(final InterviewDAO interviewDAO) {
		return this.interviewUtil.getRoundTypesThatNeedNoTaggingAgent().contains(interviewDAO.getInterviewRound())
				? Boolean.FALSE
				: Boolean.FALSE.equals(interviewDAO.getIsTaggingAgentNeeded()) ? Boolean.FALSE
						: Boolean.parseBoolean(this.staticAppConfigValues.getTaAutoAllocationEnabled());
	}

}
