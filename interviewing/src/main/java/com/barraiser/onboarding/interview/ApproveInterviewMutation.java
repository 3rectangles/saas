/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.ApproveInterviewInput;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class ApproveInterviewMutation implements NamedDataFetcher {

	private final GraphQLUtil graphQLUtil;
	private final InterviewApprovalProcessor interviewApprovalProcessor;
	private final InterviewRejectionProcessor interviewRejectionProcessor;

	@Override
	public String name() {
		return "approveInterview";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final ApproveInterviewInput input = this.graphQLUtil.getInput(environment, ApproveInterviewInput.class);
		if (input.getApproved()) {
			log.info("manually approving for interview : {} ", input.getInterviewId());
			this.interviewApprovalProcessor.approveInterview(input.getInterviewId(), user.getUserName());
		} else {
			log.info("manually rejecting for interview : {} ", input.getInterviewId());
			this.interviewRejectionProcessor.rejectInterview(input.getInterviewId(), user,
					InterviewRejectionProcessor.SOURCE_PARTNER_REJECTED);
		}
		return DataFetcherResult.newResult()
				.data(true)
				.build();
	}
}
