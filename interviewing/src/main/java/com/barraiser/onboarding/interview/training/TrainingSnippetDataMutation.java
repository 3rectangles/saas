/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training;

import com.barraiser.common.graphql.input.training.TrainingSnippetInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.onboarding.dal.TrainingSnippetDAO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.training.auth.TrainingSnippetAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TrainingSnippetDataMutation implements NamedDataFetcher {

	private final TrainingSnippetManager snippetManager;

	private final GraphQLUtil graphQLUtil;

	private final Authorizer authorizer;

	@Override
	public String name() {
		return "saveTrainingSnippets";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(dataFetchingEnvironment);
		final TrainingSnippetInput input = this.graphQLUtil
				.getInput(
						dataFetchingEnvironment,
						TrainingSnippetInput.class);

		/*
		 * final AuthorizationResourceDTO authorizationResource =
		 * AuthorizationResourceDTO.builder()
		 * .type(TrainingSnippetAuthorizer.RESOURCE_TYPE)
		 * .resource(input.getId())
		 * .build();
		 * 
		 * this.authorizer.can(
		 * user,
		 * TrainingSnippetAuthorizer.ACTION_WRITE,
		 * authorizationResource);
		 */

		TrainingSnippetDAO trainingSnippetDAO = snippetManager.saveSnippet(input, user);
		return trainingSnippetDAO.getId();
	}
}
