/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.graphql;

import com.barraiser.common.graphql.input.GetUserDetailsInput;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.QcComment;
import com.barraiser.common.graphql.types.training.TrainingSnippet;
import com.barraiser.common.utilities.FormattingUtil;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.PocEmailList;
import com.barraiser.onboarding.user.UserDetailsUtilService;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class UserDetailsDataFetcher implements MultiParentTypeDataFetcher {
	private final UserDetailsRepository userDetailsRepository;
	private final UserDetailsUtilService userDetailsUtilService;
	private final FormattingUtil formattingUtil;
	private final PocEmailList pocEmailList;
	private final GraphQLUtil graphQLUtil;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("QcComment", "commentedBy"),
				List.of("TrainingSnippet", "createdBy"),
				List.of("Evaluation", "pocs"),
				List.of(QUERY_TYPE, "getPocEmails"),
				List.of(QUERY_TYPE, "getUserDetails"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals(QUERY_TYPE) && environment.getFieldDefinition().getName().equals("getPocEmails")) {
			return DataFetcherResult.newResult()
					.data(pocEmailList.getPocEmailList(environment))
					.build();
		} else if (type.getName().equals("QcComment")) {
			final QcComment qcComment = environment.getSource();
			if (qcComment.getCommentedById() == null) {
				return DataFetcherResult.newResult().build();
			}
			return DataFetcherResult.newResult()
					.data(this.userDetailsUtilService.getUserDetailsWithoutRoles(qcComment.getCommentedById()))
					.build();
		} else if (type.getName().equals("TrainingSnippet")) {
			final TrainingSnippet trainingSnippet = environment.getSource();
			return DataFetcherResult.newResult()
					.data(this.userDetailsUtilService.getUserDetailsWithoutRoles(trainingSnippet.getUserId()))
					.build();
		} else if (type.getName().equals("Evaluation")) {
			final Evaluation evaluation = environment.getSource();
			final List<String> pocEmails = this.formattingUtil.convertStringToList(evaluation.getPocEmail(), ",");
			final List<UserDetailsDAO> users = this.userDetailsRepository.findByEmailIn(pocEmails);
			final List<UserDetailsDAO> pocs = pocEmails.stream()
					.map(
							p -> users.stream()
									.filter(u -> u.getEmail().equals(p))
									.findFirst()
									.orElse(
											UserDetailsDAO.builder()
													.email(p)
													.build()))
					.collect(Collectors.toList());
			return DataFetcherResult.newResult()
					.data(pocs.stream().map(this.userDetailsUtilService::mapUserDetails).collect(Collectors.toList()))
					.build();
		} else if (type.getName().equals(QUERY_TYPE)
				&& environment.getFieldDefinition().getName().equals("getUserDetails")) {
			final GetUserDetailsInput input = this.graphQLUtil.getInput(environment, GetUserDetailsInput.class);
			return DataFetcherResult.newResult()
					.data(this.userDetailsUtilService.getUserDetailsWithoutRoles(input.getUserId())).build();
		}
		throw new IllegalArgumentException();
	}

}
