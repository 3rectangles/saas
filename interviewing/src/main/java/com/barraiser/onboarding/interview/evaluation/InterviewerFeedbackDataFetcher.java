/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.types.*;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAllAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.dal.InterviewerFeedbackDAO;
import com.barraiser.onboarding.interview.evaluation.dal.InterviewerFeedbackRepository;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Log4j2
@Component
public class InterviewerFeedbackDataFetcher extends AuthorizedGraphQLQuery<InterviewerFeedback> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final InterviewerFeedbackRepository interviewerFeedbackRepository;
	private final InterViewRepository interViewRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;

	public InterviewerFeedbackDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAllAuthorizationInputConstructor allowAllAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			InterviewerFeedbackRepository interviewerFeedbackRepository,
			InterViewRepository interViewRepository,
			UserInformationManagementHelper userInformationManagementHelper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAllAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.interviewerFeedbackRepository = interviewerFeedbackRepository;
		this.interViewRepository = interViewRepository;
		this.userInformationManagementHelper = userInformationManagementHelper;
	}

	@Override
	protected InterviewerFeedback fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final GetInterviewerFeedbackInput input = this.graphQLUtil.getInput(environment,
				GetInterviewerFeedbackInput.class);

		final InterviewerFeedbackDAO interviewerFeedbackDAO = this.interviewerFeedbackRepository
				.findByInterviewId(input.getInterviewId()).get(0);

		final Optional<InterviewDAO> interviewDAO = this.interViewRepository.findById(interviewerFeedbackDAO.getId());

		final UserDetails Sender = this.userInformationManagementHelper
				.getUserDetailsById(interviewerFeedbackDAO.getFeedbackProviderUserId());
		UserDetails User = UserDetails.builder().build();

		if (interviewDAO.isPresent()) {
			User = this.userInformationManagementHelper.getUserDetailsById(interviewDAO.get().getInterviewerId());
		}

		return InterviewerFeedback.builder()
				.id(interviewerFeedbackDAO.getId())
				.feedback(interviewerFeedbackDAO.getFeedback())
				.createdOn(interviewerFeedbackDAO.getCreatedOn().toEpochMilli())
				.sender(Sender)
				.user(User)
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getInterviewerFeedback"));
	}
}
