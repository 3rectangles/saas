/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.JiraCommentContentCreator;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@AllArgsConstructor
public class UpdateInterviewInJiraActivity implements InterviewSchedulingActivity {
	public static final String UPDATE_INTERVIEW_IN_JIRA = "update-interview-in-jira";

	private final InterViewRepository interViewRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraCommentContentCreator jiraCommentContentCreator;
	private final InterviewStatusManager interviewStatusManager;
	private final ObjectMapper objectMapper;

	private JiraCommentDTO getCommentToBePosted(final SchedulingProcessingData data) {
		final InterviewDAO interview = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		final UserDetailsDAO interviewer = this.userDetailsRepository.findById(data.getInput().getInterviewerId())
				.get();

		final String comment = this.jiraCommentContentCreator.createInterviewOperationalUtilityContent(
				interview, interviewer, data.getIsExpertDuplicate());
		return JiraCommentDTO.builder().body(comment).build();
	}

	private void moveJiraStatus(final String interviewId) {
		final String jira = this.jiraWorkflowManager.getJiraKeyFromUUID(interviewId);
		final String currentStatus = this.jiraWorkflowManager.getJiraStatusForInterview(jira);
		final InterviewStatus toStatus = this.interviewStatusManager
				.getScheduledInterviewDestinationStatus(interviewId);
		if (List.of(InterviewStatus.PENDING_SCHEDULING.getValue(),
				InterviewStatus.SLOT_REQUESTED_BY_CANDIDATE.getValue()).contains(currentStatus)) {
			this.jiraWorkflowManager.transitionJiraStatus(
					interviewId, toStatus.getValue());
		}
	}

	private void setScheduledTimeInJira(final String interviewId, final Long startDate) {
		final OffsetDateTime startDateOffsetDateTime = OffsetDateTime.ofInstant(
				Instant.ofEpochSecond(startDate), ZoneId.of("Asia/Kolkata"));
		final InterviewServiceDeskIssue.Fields fields = InterviewServiceDeskIssue.Fields.builder()
				.scheduledTime(startDateOffsetDateTime)
				.build();
		this.jiraWorkflowManager.setInterviewFieldsInJira(interviewId, fields);
	}

	@Override
	public String name() {
		return UPDATE_INTERVIEW_IN_JIRA;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final JiraCommentDTO comment = this.getCommentToBePosted(data);
		this.moveJiraStatus(data.getInput().getInterviewId());
		this.jiraWorkflowManager.addCommentInJira(data.getInput().getInterviewId(), comment);
		this.setScheduledTimeInJira(
				data.getInput().getInterviewId(), data.getInput().getStartDate());
		return data;
	}
}
