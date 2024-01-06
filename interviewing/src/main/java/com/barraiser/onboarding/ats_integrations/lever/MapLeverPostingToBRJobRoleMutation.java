/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.LeverPostingToBRJobRoleHandler;
import com.barraiser.common.graphql.input.MapLeverPostingToBRJobRoleInput;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class MapLeverPostingToBRJobRoleMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final LeverPostingToBRJobRoleHandler leverPostingToBRJobRoleHandler;

	@Override
	public String name() {
		return "mapLeverPostingToBRJobRole";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final MapLeverPostingToBRJobRoleInput input = this.graphQLUtil
				.getInput(
						environment,
						MapLeverPostingToBRJobRoleInput.class);

		this.leverPostingToBRJobRoleHandler.attachLeverPostingToBRJobRole(input);

		return true;
	}

}
