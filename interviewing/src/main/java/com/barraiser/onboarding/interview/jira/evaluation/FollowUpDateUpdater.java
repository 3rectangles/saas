/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.interview.evaluation.EvaluationChangeHistoryManager;
import com.barraiser.onboarding.interview.jira.JiraFieldManager;
import com.barraiser.onboarding.interview.jira.dto.JiraChangeLogsResponse;
import com.barraiser.onboarding.scheduling.followup.StartFollowUpForSchedulingStepFunctionProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Log4j2
public class FollowUpDateUpdater {
	public static final String FOLLOW_UP_DATE_FIELD_ID = "customfield_10314";

	public static final String FOLLOW_UP_DATE_FIELD_KEY = "follow_up_date";
	private final EvaluationChangeHistoryManager evaluationChangeHistoryManager;
	private final JiraFieldManager jiraFieldManager;
	private final StartFollowUpForSchedulingStepFunctionProcessor startFollowUpForSchedulingStepFunctionProcessor;

	@Transactional
	public void update(final EvaluationDAO originalEvaluation, final EvaluationDAO updatedEvaluation,
			final String jiraKey) {
		final List<JiraChangeLogsResponse.ChangeLog> changeLogs = this.jiraFieldManager.getChangeLogsForField(jiraKey,
				FOLLOW_UP_DATE_FIELD_ID);
		final Boolean isFollowUpDateUpdated = this.checkIfFollowUpDateFieldUpdated(changeLogs,
				updatedEvaluation.getId());
		this.evaluationChangeHistoryManager.removeHistoryForField(updatedEvaluation.getId(), FOLLOW_UP_DATE_FIELD_KEY);
		this.addFieldHistoryInDB(updatedEvaluation, changeLogs);
		if ((isFollowUpDateUpdated || !Objects.equals(originalEvaluation.getStatus(), updatedEvaluation.getStatus()))
				&& Objects.equals(updatedEvaluation.getStatus(), EvaluationStatus.WAITING_CANDIDATE.getValue()))
			this.triggerFollowUpStepFunction(updatedEvaluation);
	}

	private void triggerFollowUpStepFunction(final EvaluationDAO updatedEvaluation) {
		try {
			final EvaluationServiceDeskProcessingData data = new EvaluationServiceDeskProcessingData();
			data.setEvaluationDAO(updatedEvaluation);
			final String currentFollowUpValue = this.evaluationChangeHistoryManager
					.getCurrentFieldValue(FOLLOW_UP_DATE_FIELD_KEY, updatedEvaluation.getId());
			data.setFollowUpDate(currentFollowUpValue);
			this.startFollowUpForSchedulingStepFunctionProcessor.process(data);
		} catch (final Exception e) {
			log.info(String.format(
					"Unable to trigger follow-up for scheduling state machine for evaluation id: %s",
					updatedEvaluation.getId()));
			e.printStackTrace();
		}
	}

	private void addFieldHistoryInDB(final EvaluationDAO originalEvaluation,
			final List<JiraChangeLogsResponse.ChangeLog> changeLogs) {
		for (final JiraChangeLogsResponse.ChangeLog changeLog : changeLogs) {
			for (final JiraChangeLogsResponse.ChangeLog.Item item : changeLog.getItems()) {
				if (item.getFieldId().equals(FOLLOW_UP_DATE_FIELD_ID)) {
					this.evaluationChangeHistoryManager.saveHistory(originalEvaluation.getId(),
							FOLLOW_UP_DATE_FIELD_KEY, item.getToString(),
							changeLog.getAuthor().getDisplayName(), changeLog.getCreated().toInstant());
				}
			}
		}
	}

	private Boolean checkIfFollowUpDateFieldUpdated(final List<JiraChangeLogsResponse.ChangeLog> changeLogs,
			final String evaluationId) {

		// Sometimes the change logs
		if (changeLogs.size() == 0)
			return Boolean.FALSE;

		final String currentFollowUpFieldValue = this.evaluationChangeHistoryManager
				.getCurrentFieldValue(FOLLOW_UP_DATE_FIELD_KEY, evaluationId);
		final String lastUpdatedChangeLogForFollowUpField = changeLogs.get(changeLogs.size() - 1).getItems().get(0)
				.getToString();
		return !Objects.equals(lastUpdatedChangeLogForFollowUpField, currentFollowUpFieldValue);
	}
}
