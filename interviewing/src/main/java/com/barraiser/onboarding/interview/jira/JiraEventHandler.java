package com.barraiser.onboarding.interview.jira;

import com.barraiser.onboarding.interview.jira.dto.JiraEvent;

/**
 * Handles events coming through JIRA webhook
 */
public interface JiraEventHandler {
    String projectId();

    void handleEvent(final JiraEvent event) throws Exception;
}
