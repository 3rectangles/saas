/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import java.util.List;

import org.springframework.stereotype.Component;

import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigDAO;
import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DeleteInterceptionKeyword extends AuthorizedGraphQLMutation {

	final RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository;

	public DeleteInterceptionKeyword(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.relaxedMeetingInterceptionConfigRepository = relaxedMeetingInterceptionConfigRepository;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {

		final String partnerId = environment.getArgument("partnerId");
		final String keyword = environment.getArgument("keyword");
		RelaxedMeetingInterceptionConfigDAO relaxedMeetingInterceptionConfigDAO = this.relaxedMeetingInterceptionConfigRepository
				.findByPartnerId(partnerId).orElse(null);
		if (relaxedMeetingInterceptionConfigDAO == null) {
			return Boolean.FALSE;
		}
		List<String> updatedKeywords = relaxedMeetingInterceptionConfigDAO.getKeyword();
		boolean isRemoved = updatedKeywords.remove(keyword);
		if (!isRemoved) {
			return Boolean.FALSE;
		}
		this.relaxedMeetingInterceptionConfigRepository
				.save(relaxedMeetingInterceptionConfigDAO.toBuilder().keyword(updatedKeywords).build());
		return Boolean.TRUE;
	}

	@Override
	public String name() {
		return "deleteInterceptionKeyword";
	}

}
