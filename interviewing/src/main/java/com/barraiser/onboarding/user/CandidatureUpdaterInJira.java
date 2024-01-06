/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.common.graphql.input.CandidatureDetailsToBeUpdated;
import com.barraiser.common.graphql.input.UpdateCandidatureInput;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CandidatureUpdaterInJira {

	private final JiraWorkflowManager jiraWorkflowManager;

	public void updateUserFieldsInJira(final UpdateCandidatureInput updateCandidatureDetailsInput) {
		final UserDetailsInput detailsToBeUpdated = updateCandidatureDetailsInput
				.getCandidatureDetailsToBeUpdated().getUserDetailsInput();
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.email(detailsToBeUpdated.getEmail())
				.phone(detailsToBeUpdated.getPhone())
				.fullName(detailsToBeUpdated.getFullName())
				.designation(detailsToBeUpdated.getDesignation())
				.almaMater(detailsToBeUpdated.getAlmaMater())
				.currentCompany(detailsToBeUpdated.getCurrentCompanyName())
				.workExperience(detailsToBeUpdated.getWorkExperienceInMonths() != null
						? detailsToBeUpdated.getWorkExperienceInMonths().toString()
						: null)
				.timezone(detailsToBeUpdated.getTimezone() != null
						? IdValueField.builder().value(detailsToBeUpdated.getTimezone()).build()
						: null)
				.pocEmail(updateCandidatureDetailsInput.getCandidatureDetailsToBeUpdated().getPocEmail())
				.build();
		this.jiraWorkflowManager.setEvaluationFieldsInJira(updateCandidatureDetailsInput.getEvaluationId(),
				updatedFields);
	}
}
