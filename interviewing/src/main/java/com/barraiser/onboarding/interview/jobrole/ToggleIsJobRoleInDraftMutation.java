/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.common.graphql.input.ToggleIsJobRoleInDraftInput;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.StatusMapper;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.jobRoleManagement.JobRole.search.JobRoleElasticsearchManager;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
public class ToggleIsJobRoleInDraftMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final JobRoleManager jobRoleManager;
	private final JobRoleStatusManager jobRoleStatusManager;

	public ToggleIsJobRoleInDraftMutation(
			final ToggleIsJobRoleInDraftAuthorizer authorizer,
			final GraphQLUtil graphQLUtil,
			final JobRoleManager jobRoleManager,
			final JobRoleStatusManager jobRoleStatusManager) {
		super(authorizer);
		this.graphQLUtil = graphQLUtil;
		this.jobRoleManager = jobRoleManager;
		this.jobRoleStatusManager = jobRoleStatusManager;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException {
		final ToggleIsJobRoleInDraftInput input = this.graphQLUtil.getInput(environment,
				ToggleIsJobRoleInDraftInput.class);
		this.toggleIsJobRoleInDraft(input.getJobRoleId(), input.getJobRoleVersion(), input.getIsDraft());
		return Boolean.TRUE;
	}

	public void toggleIsJobRoleInDraft(final String jobRoleId, final Integer jobRoleVersion, final Boolean isDraft)
			throws IOException {
		final JobRoleDAO jobRoleDAO = this.jobRoleManager
				.getJobRole(jobRoleId, jobRoleVersion).get();

		JobRoleDAO updatedJobRoleDAO = jobRoleDAO.toBuilder()
				.isDraft(Boolean.TRUE.equals(isDraft))
				.build();

		updatedJobRoleDAO = updatedJobRoleDAO.toBuilder()
				.brStatus(this.jobRoleStatusManager.getBrStatus(updatedJobRoleDAO))
				.build();

		this.jobRoleManager.saveJobRole(updatedJobRoleDAO);
	}

	@Override
	public String name() {
		return "toggleIsJobRoleInDraft";
	}
}
