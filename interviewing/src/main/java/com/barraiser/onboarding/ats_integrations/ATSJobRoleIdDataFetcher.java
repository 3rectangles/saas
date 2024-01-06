/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.dto.ATSJobRoleDTO;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.graphql.*;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ATSJobRoleIdDataFetcher extends AuthorizedGraphQLQuery_deprecated<String> {
	private final ATSServiceClient atsServiceClient;

	public ATSJobRoleIdDataFetcher(
			final AllowAllAuthorizer authorizer,
			final ObjectFieldsFilter<String> objectFieldsFilter,
			final ATSServiceClient atsServiceClient) {
		super(authorizer, objectFieldsFilter);
		this.atsServiceClient = atsServiceClient;
	}

	@Override
	protected String fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final JobRole jobRole = environment.getSource();
		final ATSJobRoleDTO atsJobRole = this.atsServiceClient.getAtsJobRoleIdFromBrJobRoleId(jobRole.getId())
				.getBody();
		if (atsJobRole == null) {
			return null;
		}
		return atsJobRole.getId();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("JobRole", "atsId"));
	}
}
