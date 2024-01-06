/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.ta;

import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class TaJiraProcessor implements SchedulingProcessor {

	private final JiraWorkflowManager jiraWorkflowManager;
	private final String COMMENT_TA_SHORTAGE = "There is no TA available, kindly look for TAs manually";

	@Override
	public void process(final SchedulingProcessingData data) {
		if (!data.getExecuteTaAssignment())
			return;
		if (!data.getIsTaAllocated()) {
			final JiraCommentDTO comment = JiraCommentDTO.builder().body(COMMENT_TA_SHORTAGE).build();
			this.jiraWorkflowManager.addCommentInJira(data.getInput().getInterviewId(), comment);
		} else {
			log.info("Ta already allocated for interview ID: {}", data.getInput().getInterviewId());
		}

	}

}
