/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.common.graphql.types.InterviewStructure;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.ats_integrations.dto.ATSInterviewStructureDTO;
import com.barraiser.onboarding.graphql.AllowAllAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ATSInterviewStructureIdDataFetcher extends AuthorizedGraphQLQuery_deprecated<String> {
	private final ATSServiceClient atsServiceClient;

	public ATSInterviewStructureIdDataFetcher(
			final AllowAllAuthorizer authorizer,
			final ObjectFieldsFilter<String> objectFieldsFilter,
			final ATSServiceClient atsServiceClient) {
		super(authorizer, objectFieldsFilter);
		this.atsServiceClient = atsServiceClient;
	}

	@Override
	protected String fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final InterviewStructure interviewStructure = environment.getSource();
		final ATSInterviewStructureDTO atsInterviewStructure = this.atsServiceClient
				.getAtsInterviewStructureId(interviewStructure.getId()).getBody();
		if (atsInterviewStructure == null) {
			return null;
		}
		return atsInterviewStructure.getId();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("InterviewStructure", "atsId"));
	}
}
