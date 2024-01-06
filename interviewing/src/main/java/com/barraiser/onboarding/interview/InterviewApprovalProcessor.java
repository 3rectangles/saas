/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewApprovalProcessor {
	public static final String COMMENT_FOR_APPROVAL_REQUIRED_FROM_PARTNER = "Please review round level score and suggest if we have to proceed "
			+
			"with the next round";

	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterviewsCreatedEventGenerator eventGenerator;
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewCreatorInDB interviewCreatorInDB;
	private final InterviewCreatorInJira interviewCreatorInJira;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final PartnerConfigManager partnerConfigManager;
	private final JiraEvaluationStatusUpdatorForScheduling jiraEvaluationStatusUpdatorForScheduling;

	public void approveInterview(final String interviewId, final String userId) {
		final EvaluationDAO evaluationDAO = this.interviewUtil.getEvaluationForInterview(interviewId);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		this.transitionEvaluationStatus(interviewDAO, evaluationDAO, userId);
		this.createNextInterviewRounds(interviewDAO, evaluationDAO, userId);
	}

	public void takeActionForApprovalRequiredFromClient(final EvaluationDAO evaluation) {
		final EvaluationDAO updatedEvaluation = this.evaluationStatusManager.transitionBarRaiserStatus(
				evaluation.getId(), EvaluationStatus.WAITING_CLIENT.getValue(),
				EvaluationStatusManager.BARRAISER_PARTNER_ID);
		this.jiraWorkflowManager.transitionJiraStatus(updatedEvaluation.getId(),
				EvaluationStatus.WAITING_CLIENT.getValue());
		this.jiraWorkflowManager.addCommentInJira(updatedEvaluation.getId(),
				JiraCommentDTO.builder().body(COMMENT_FOR_APPROVAL_REQUIRED_FROM_PARTNER).build());
	}

	public void createNextInterviewRounds(final InterviewDAO interviewDAO, final EvaluationDAO evaluationDAO,
			final String userId) {
		final Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion(), interviewDAO.getInterviewStructureId());
		final List<InterviewDAO> interviews = this.interviewCreatorInDB.createInterviewInDatabase(evaluationDAO,
				jobRoleToInterviewStructureDAO.get().getOrderIndex(), userId);
		this.interviewCreatorInJira.createInterviewsInJira(interviews,
				this.jiraUUIDRepository.findByUuid(evaluationDAO.getId()).get().getJira());
		this.eventGenerator.sendInterviewsCreatedEvent(evaluationDAO.getId(),
				interviews.stream().map(InterviewDAO::getId).collect(Collectors.toList()));
	}

	private void transitionEvaluationStatus(final InterviewDAO interviewDAO, final EvaluationDAO evaluationDAO,
			final String userId) {
		final Boolean isCandidateSchedulingAllowed = this.partnerConfigManager
				.shouldSendSchedulingLinkToCandidate(interviewDAO);
		this.jiraEvaluationStatusUpdatorForScheduling.transition(
				evaluationDAO, userId, isCandidateSchedulingAllowed, interviewDAO.getId());

	}
}
