/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;

import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigDAO;
import com.barraiser.onboarding.dal.RelaxedMeetingInterceptionConfigRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;

import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UpsertInterceptionKeyword extends AuthorizedGraphQLMutation {

	final RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository;

	public UpsertInterceptionKeyword(final AuthorizationServiceFeignClient authorizationServiceFeignClient,
			final AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			final RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.relaxedMeetingInterceptionConfigRepository = relaxedMeetingInterceptionConfigRepository;
	}

	@Override
	protected Boolean fetch(final DataFetchingEnvironment environment, final AuthorizationResult authorizationResult)
			throws Exception {
		final String partnerId = environment.getArgument("partnerId");
		final List<String> keyword = environment.getArgument("keyword");
		RelaxedMeetingInterceptionConfigDAO relaxedMeetingInterceptionConfigDAO = this.relaxedMeetingInterceptionConfigRepository
				.findByPartnerId(partnerId).orElse(new RelaxedMeetingInterceptionConfigDAO());
		List<String> addKeywords = relaxedMeetingInterceptionConfigDAO.getKeyword() == null ? new ArrayList<>()
				: relaxedMeetingInterceptionConfigDAO.getKeyword();
		addKeywords.addAll(keyword);
		this.relaxedMeetingInterceptionConfigRepository.save(relaxedMeetingInterceptionConfigDAO.toBuilder()
				.keyword(addKeywords).partnerId(partnerId).build());
		return Boolean.TRUE;
	}

	@Override
	public String name() {
		return "addInterceptionKeyword";
	}

}
