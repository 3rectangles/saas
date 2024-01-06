/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.common.utilities.EmailParser;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.interview.jira.JiraEventHandler;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.ExpertIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraEvent;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
class ExpertHandler implements JiraEventHandler {
	public static final String JIRA_ISSUE_TYPE_ID_EXPERT = "10085";
	private final JiraUUIDRepository jiraUUIDRepository;
	private final UserInformationManagementHelper userManagement;
	private final JiraUtil jiraUtil;
	private final JiraClient jiraClient;
	private final UserDetailsUpdater userDetailsUpdater;
	private final ExpertSpecificDetailsUpdater expertSpecificDetailsUpdater;

	@Override
	public void handleEvent(final JiraEvent event) throws Exception {
		ExpertIssue expert = this.jiraClient.getExpertIssue(event.getIssue());
		ExpertIssue.Fields fields = expert.getFields();
		if (fields.getEmail() == null) {
			return;
		}
		fields = fields.toBuilder().email(fields.getEmail().trim()).build();
		expert = expert.toBuilder().fields(fields).build();
		EmailParser.validateEmail(fields.getEmail());

		// First create the user if it does not exist.
		Optional<String> userId = this.userManagement.findUserByEmail(fields.getEmail());
		if (userId.isEmpty()) {
			userId = this.jiraUUIDRepository.findByJira(expert.getKey()).map(JiraUUIDDAO::getUuid);
		}

		final UserDetailsDAO userDetails;
		if (userId.isEmpty()) {
			userDetails = this.userManagement.getOrCreateUserByEmail(fields.getEmail());
			// Map the userId with the EP so that correct user is updated on next sync.
		} else {
			userDetails = this.userManagement.findUserById(userId.get());
		}
		this.jiraUtil.getOrCreateIdAgainstJira(expert.getKey(), userDetails.getId());

		final UserDetailsDAO updatedUser = this.userDetailsUpdater.createOrUpdateUserDetails(userDetails.getId(),
				fields);
		this.expertSpecificDetailsUpdater.createOrUpdateExpertDetails(fields, updatedUser);
	}

	@Override
	public String projectId() {
		return JIRA_ISSUE_TYPE_ID_EXPERT;
	}
}
