/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.BgsEnquiryInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class BgsEnquiryMutation implements NamedDataFetcher<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final BgsEnquiryManager bgsEnquiryManager;

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final BgsEnquiryInput input = this.graphQLUtil.getInput(environment, BgsEnquiryInput.class);

		this.bgsEnquiryManager.logBgsEnquiry(input.getCandidateId(), input.getInterested());

		return Boolean.TRUE;
	}

	@Override
	public String name() {
		return "addBgsEnquiry";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}
}
