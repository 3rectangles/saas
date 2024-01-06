/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.sfn.InterviewSchedulingActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class TransitionInterviewToPendingInterviewingActivity implements InterviewSchedulingActivity {
	public static final String TRANSITION_INTERVIEW_TO_PENDING_INTERVIEWING = "transition-interview-to-pending-interviewing";

	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;
	private final ObjectMapper objectMapper;

	private void moveJiraToPendingInterview(final String interviewId) {
		this.jiraWorkflowManager.transitionJiraStatus(
				interviewId, InterviewStatus.PENDING_INTERVIEWING.getValue());
	}

	@Override
	public String name() {
		return TRANSITION_INTERVIEW_TO_PENDING_INTERVIEWING;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		InterviewDAO interviewDAO = interViewRepository.findById(data.getInput().getInterviewId()).get();
		if (Objects.isNull(interviewDAO.getTaggingAgent())
				|| InterviewStatus.PENDING_INTERVIEWING.getValue().equalsIgnoreCase(interviewDAO.getStatus()))
			return data;
		interviewDAO = interviewDAO.toBuilder().status(InterviewStatus.PENDING_INTERVIEWING.getValue()).build();
		interviewDAO = this.interviewService.save(interviewDAO);
		this.moveJiraToPendingInterview(interviewDAO.getId());
		return data;
	}
}
