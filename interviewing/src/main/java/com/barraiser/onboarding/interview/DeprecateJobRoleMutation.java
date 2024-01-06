/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.DeprecateJobRoleInput;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleStatusManager;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class DeprecateJobRoleMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final JobRoleStatusManager jobRoleStatusManager;
	private final JobRoleManager jobRoleManager;

	@Override
	public String name() {
		return "deprecateJobRole";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final DeprecateJobRoleInput input = this.graphQLUtil.getInput(
				environment,
				DeprecateJobRoleInput.class);

		this.deprecateJobRole(input.getJobRoleId(), input.getJobRoleVersion());

		return true;
	}

	public void deprecateJobRole(final String jobRoleId, final Integer jobRoleVersion) throws IOException {
		try {
			final JobRoleDAO jobRoleDAO = this.jobRoleManager
					.getJobRole(jobRoleId, jobRoleVersion).get();

			final String jobRoleDocumentId = String.format("%s|%s", jobRoleDAO.getEntityId().getId(),
					jobRoleDAO.getEntityId().getVersion());

			JobRoleDAO updatedJobRoleDAO = jobRoleDAO.toBuilder()
					.deprecatedOn(Instant.now())
					.build();

			updatedJobRoleDAO = updatedJobRoleDAO.toBuilder()
					.brStatus(this.jobRoleStatusManager.getBrStatus(updatedJobRoleDAO)).build();

			this.jobRoleManager.saveJobRole(updatedJobRoleDAO);

		} catch (final Exception exception) {
			log.error("Job role does not exist : ", exception);
			throw exception;
		}
	}
}
