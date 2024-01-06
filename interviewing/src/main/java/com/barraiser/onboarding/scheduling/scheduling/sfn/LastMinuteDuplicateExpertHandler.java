/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertAllocationProcessor;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class LastMinuteDuplicateExpertHandler implements InterviewSchedulingActivity, ExpertAllocationProcessor {
	public static final String CANDIDATE_CONFIRMATION_PRIORITY_FLAG = "Candidate_Confirmed_To_Join";
	public static final String ERROR_MESSAGE = "Team, there was some error while getting interviewers for this " +
			"overbooked interview. Please contact the tech team at the earliest";
	public static final String MESSAGE_TO_NOTIFY_TEAM_FOR_ELIGIBLE_EXPERTS = "Team, Please check for eligible experts from the attached link : %s";
	public static final String EXPERT_FINDER_LINK = "https://app.barraiser.com/expert-finder?interview=%s";

	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterviewConfirmationRepository interviewConfirmationRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;
	private final ExpertUtil expertUtil;
	private final ObjectMapper objectMapper;
	public static final String HANDLE_DUPLICATE_EXPERT = "handle-duplicate-expert";

	public void handleLastMinuteDuplicateExpert(final InterviewDAO interviewDAO) {
		this.transitionEvaluationStatus(interviewDAO.getEvaluationId());
		this.transitionInterviewStatus(interviewDAO);
		if (Boolean.TRUE.equals(this.shouldAddPriorityFlag(interviewDAO))) {
			this.setPriorityFlagInInterview(interviewDAO.getId());
		}
		try {
			this.postCommentForEligibleExpertsOnJira(interviewDAO.getId());
		} catch (final Exception e) {
			log.error(e, e);
			this.jiraWorkflowManager.addCommentInJira(interviewDAO.getId(),
					JiraCommentDTO.builder().body(ERROR_MESSAGE).build());
		}
	}

	private void transitionEvaluationStatus(final String evaluationId) {
		this.evaluationStatusManager.transitionBarRaiserStatus(
				evaluationId,
				EvaluationStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue(),
				EvaluationStatusManager.BARRAISER_PARTNER_ID);
		this.jiraWorkflowManager.transitionJiraStatus(
				evaluationId, EvaluationStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue());
	}

	private void transitionInterviewStatus(InterviewDAO interviewDAO) {
		interviewDAO = interviewDAO.toBuilder()
				.status(InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue())
				.build();
		interviewDAO = this.interviewService.save(interviewDAO);
		this.jiraWorkflowManager.transitionJiraStatus(
				interviewDAO.getId(), InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue());
	}

	private void setPriorityFlagInInterview(final String interviewId) {
		final InterviewServiceDeskIssue currentIssue = this.jiraWorkflowManager.getInterviewIssue(interviewId);
		final List<String> priorityFlags = Objects.requireNonNullElse(
				currentIssue.getFields().getPriorityFlags(), new ArrayList<>());
		if (!priorityFlags.contains(CANDIDATE_CONFIRMATION_PRIORITY_FLAG)) {
			priorityFlags.add(CANDIDATE_CONFIRMATION_PRIORITY_FLAG);
		}
		final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags).build();

		this.jiraWorkflowManager.setInterviewFieldsInJira(interviewId, updatedFields);
	}

	private Boolean shouldAddPriorityFlag(final InterviewDAO interviewDAO) {
		final Optional<InterviewConfirmationDAO> interviewConfirmationDAO = this.interviewConfirmationRepository
				.findTopByInterviewIdAndRescheduleCountOrderByCandidateConfirmationTimeDesc(
						interviewDAO.getId(), interviewDAO.getRescheduleCount());
		return interviewConfirmationDAO
				.map(InterviewConfirmationDAO::getCandidateConfirmation)
				.orElse(null);
	}

	@Override
	public void process(final ExpertAllocatorData data) throws IOException {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		if (this.expertUtil.isExpertDuplicate(interviewDAO.getInterviewerId())) {
			this.handleLastMinuteDuplicateExpert(interviewDAO);
		}
	}

	private void postCommentForEligibleExpertsOnJira(final String interviewId) {
		final String comment = String.format(MESSAGE_TO_NOTIFY_TEAM_FOR_ELIGIBLE_EXPERTS,
				String.format(EXPERT_FINDER_LINK, interviewId));
		final JiraCommentDTO jiraCommentDTO = JiraCommentDTO.builder().body(comment).build();
		this.jiraWorkflowManager.addCommentInJira(interviewId, jiraCommentDTO);
	}

	@Override
	public String name() {
		return HANDLE_DUPLICATE_EXPERT;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		this.handleLastMinuteDuplicateExpert(interviewDAO);
		return data;
	}
}
