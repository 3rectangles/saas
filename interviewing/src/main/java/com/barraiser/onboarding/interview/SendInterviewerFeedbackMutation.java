/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.*;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAllAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.dal.InterviewerFeedbackDAO;
import com.barraiser.onboarding.interview.evaluation.dal.InterviewerFeedbackRepository;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
public class SendInterviewerFeedbackMutation extends AuthorizedGraphQLMutation<InterviewerFeedback> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final InterviewerFeedbackRepository interviewerFeedbackRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final SendInterviewerFeedbackEventGenerator sendInterviewerFeedbackEventGenerator;
	private final InterViewRepository interViewRepository;

	public SendInterviewerFeedbackMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAllAuthorizationInputConstructor allowAllAuthorizationInputConstructor,
			InterviewerFeedbackRepository interviewerFeedbackRepository,
			UserInformationManagementHelper userInformationManagementHelper,
			SendInterviewerFeedbackEventGenerator sendInterviewerFeedbackEventGenerator,
			InterViewRepository interViewRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAllAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.interviewerFeedbackRepository = interviewerFeedbackRepository;
		this.sendInterviewerFeedbackEventGenerator = sendInterviewerFeedbackEventGenerator;
		this.userInformationManagementHelper = userInformationManagementHelper;
		this.interViewRepository = interViewRepository;
	}

	@Override
	protected InterviewerFeedback fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final InterviewerFeedbackInput input = this.graphQLUtil.getInput(environment, InterviewerFeedbackInput.class);

		InterviewerFeedbackDAO savedInterviewerFeedback = this.saveInterviewerFeedback(input, authenticatedUser);

		this.sendInterviewerFeedbackEventGenerator.sendInterviewerFeedbackEvent(input, authenticatedUser);

		return this.interviewerFeedbackDAOtoInterviewerFeedbackMapper(savedInterviewerFeedback);
	}

	private InterviewerFeedbackDAO saveInterviewerFeedback(InterviewerFeedbackInput input,
			AuthenticatedUser authenticatedUser) {
		final InterviewerFeedbackDAO interviewDAO = InterviewerFeedbackDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(input.getInterviewId())
				.feedback(input.getFeedback())
				.feedbackProviderUserId(authenticatedUser.getUserName())
				.offsetTime(input.getOffsetTime())
				.interviewerId(input.getInterviewerId())
				.mailList(input.getCcUserList())
				.build();

		return this.interviewerFeedbackRepository.save(interviewDAO);
	}

	private InterviewerFeedback interviewerFeedbackDAOtoInterviewerFeedbackMapper(
			InterviewerFeedbackDAO interviewerFeedbackDAO) {

		final UserDetails sender = this.userInformationManagementHelper
				.getUserDetailsById(interviewerFeedbackDAO.getFeedbackProviderUserId());
		final UserDetails user = this.userInformationManagementHelper
				.getUserDetailsById(interviewerFeedbackDAO.getInterviewerId());

		return InterviewerFeedback.builder()
				.id(interviewerFeedbackDAO.getId())
				.feedback(interviewerFeedbackDAO.getFeedback())
				.createdOn(interviewerFeedbackDAO.getCreatedOn().toEpochMilli())
				.sender(sender)
				.user(user)
				.build();
	}

	@Override
	public String name() {
		return "sendInterviewerFeedback";
	}

}
