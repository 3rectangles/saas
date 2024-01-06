/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;

@Component
@AllArgsConstructor
public class InterviewStatusManager {
	private final StatusRepository statusRepository;
	private final Map<String, String> mapStatusToDisplayStatus = new HashMap<>();
	private final InterviewService interviewService;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;

	public Map<String, String> getMapOfStatusToDisplayStatus() {
		return this.mapStatusToDisplayStatus;
	}

	@PostConstruct
	private void fetchStatusToDisplayStatusMapping() {
		this.statusRepository.findAllByEntityType("interview").forEach(
				x -> this.mapStatusToDisplayStatus.put(x.getInternalStatus(), x.getDisplayStatus()));
	}

	public StatusDAO getStatusOfInterview(final InterviewStatus status) {
		return this.statusRepository.findByInternalStatusAndPartnerIdAndEntityType(status.getValue(), null,
				"interview").get();
	}

	@Transactional
	public InterviewDAO updateInterviewStatus(final InterviewDAO interviewDAO, final InterviewStatus status,
			final String createdBy, final String source) {
		final InterviewDAO updatedInterview = this.interviewService
				.save(interviewDAO.toBuilder().status(status.getValue()).build(), createdBy, source);
		this.jiraWorkflowManager.transitionJiraStatus(interviewDAO.getId(), status.getValue());
		return updatedInterview;
	}

	public InterviewStatus getScheduledInterviewDestinationStatus(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();

		if (this.interviewUtil.isFastrackedInterview(interviewDAO.getInterviewRound())
				|| Boolean.FALSE.equals(interviewDAO.getIsTaggingAgentNeeded())) {
			return InterviewStatus.PENDING_INTERVIEWING;
		}

		return InterviewStatus.PENDING_TA_ASSIGNMENT;
	}
}
