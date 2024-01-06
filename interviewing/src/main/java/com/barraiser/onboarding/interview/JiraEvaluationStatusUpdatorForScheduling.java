/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;

import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@AllArgsConstructor
public class JiraEvaluationStatusUpdatorForScheduling {
	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final DateUtils dateUtils;
	private final TimezoneManager timezoneManager;

	public void transition(
			final EvaluationDAO evaluationDAO,
			final String userId,
			final Boolean isCandidateSchedulingAllowed, final String interviewId) {
		if (isCandidateSchedulingAllowed) {
			final String timezone = this.timezoneManager.getTimezoneOfCandidate(interviewId);
			final OffsetDateTime followUpDateInJira = OffsetDateTime.ofInstant(
					Instant.ofEpochSecond(this.getFollowUpTime(timezone)),
					ZoneId.of(DateUtils.TIMEZONE_ASIA_KOLKATA));
			final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
					.followUpDate(followUpDateInJira)
					.waitingReasonCategory(IdValueField.builder().value("Other").build())
					.waitingReasonForEvaluation(
							"Email/Whatsapp is shared waiting for candidate reply")
					.build();
			if (!EvaluationStatus.WAITING_CANDIDATE.getValue().equals(evaluationDAO.getStatus())) {
				this.evaluationStatusManager.transitionBarRaiserStatus(
						evaluationDAO.getId(), EvaluationStatus.WAITING_CANDIDATE.getValue(), userId);
				this.jiraWorkflowManager.updateTransitionScreenFieldsAndTransitionJiraStatus(
						this.jiraUUIDRepository.findByUuid(evaluationDAO.getId()).get().getJira(),
						updatedFields,
						EvaluationStatus.WAITING_CANDIDATE.getValue());
			}
		} else {
			if (!EvaluationStatus.WAITING_CLIENT.getValue().equals(evaluationDAO.getStatus())) {
				this.evaluationStatusManager.transitionBarRaiserStatus(
						evaluationDAO.getId(), EvaluationStatus.WAITING_CLIENT.getValue(), userId);
				this.jiraWorkflowManager.transitionJiraStatus(
						evaluationDAO.getId(), EvaluationStatus.WAITING_CLIENT.getValue());
			}
		}
	}

	private Long getFollowUpTime(final String timezone) {
		final long currentTime = System.currentTimeMillis() / 1000;
		long followUpTime = currentTime + 2 * 60 * 60;
		final Long startOfDay = this.dateUtils.getStartOfDayEpochSecond(
				followUpTime, timezone);
		final long maxTimeOfFollowUp = startOfDay + 22 * 60 * 60;
		final long minTimeOfFollowUp = startOfDay + 8 * 60 * 60;
		if ((followUpTime > maxTimeOfFollowUp) || (followUpTime < minTimeOfFollowUp)) {
			followUpTime = followUpTime < minTimeOfFollowUp
					? minTimeOfFollowUp
					: minTimeOfFollowUp + 24 * 60 * 60;
		}
		return followUpTime;
	}
}
