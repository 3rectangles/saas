/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training.auth;

import com.barraiser.common.graphql.input.training.TrainingSnippetInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class TrainingSnippetAuthorizer implements ResourceAuthorizer {

	public final static String RESOURCE_TYPE = "trainingSnippet";

	public final static String ACTION_READ = "read";
	public final static String ACTION_WRITE = "write";

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(AuthenticatedUser user, String action, Object resource) throws AuthorizationException {
		if (ACTION_READ.equals(action)) {
			this.canRead(user, (TrainingSnippetInput) resource);
			return;
		}
		if (ACTION_WRITE.equals(action)) {
			this.canWrite(user, (TrainingSnippetInput) resource);
			return;
		}
		throw new IllegalArgumentException(
				"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
	}

	private void canRead(final AuthenticatedUser user, final TrainingSnippetInput trainingSnippetInput) {
		if (this.isSuperUser(user)) {
			return;
		}
		if (StringUtils.isNotEmpty(trainingSnippetInput.getUserId())
				&& !user.getUserName().equals(trainingSnippetInput.getUserId())) {
			throw new AuthorizationException();
		}
	}

	private void canWrite(final AuthenticatedUser user, final TrainingSnippetInput trainingSnippetInput) {
		if (this.isSuperUser(user)) {
			return;
		}
		if (trainingSnippetInput.getUserId() != null
				&& !user.getUserName().equals(trainingSnippetInput.getUserId())) {
			throw new AuthorizationException();
		}
	}
}
