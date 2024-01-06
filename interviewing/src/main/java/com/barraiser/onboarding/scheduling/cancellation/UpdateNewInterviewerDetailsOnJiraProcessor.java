/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.JiraCommentContentCreator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateNewInterviewerDetailsOnJiraProcessor implements CancellationProcessor {
	private final UserDetailsRepository userDetailsRepository;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraCommentContentCreator jiraCommentContentCreator;

	@Override
	public void process(final CancellationProcessingData data) {
		this.updateDetailsOnJira(
				data.getInterviewThatExpertCanTake(),
				data.getPreviousStateOfCancelledInterview().getInterviewerId());
	}

	private void updateDetailsOnJira(final InterviewDAO interviewDAO, final String interviewerId) {
		final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interviewerId).get();
		final JiraCommentDTO comment = JiraCommentDTO.builder()
				.body(
						this.jiraCommentContentCreator
								.createInterviewOperationalUtilityContent(
										interviewDAO, interviewer, false))
				.build();
		this.jiraWorkflowManager.addCommentInJira(interviewDAO.getId(), comment);
	}
}
