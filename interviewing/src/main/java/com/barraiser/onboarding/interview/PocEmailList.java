/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class PocEmailList {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepsRepository partnerRepsRepository;
	private final UserDetailsRepository userDetailsRepository;

	public List<UserDetailsDAO> getPocEmailList(final DataFetchingEnvironment environment) throws Exception {
		final PartnerInput input = this.graphQLUtil.getInput(environment, PartnerInput.class);

		final List<String> partnerRepIds = this.partnerRepsRepository.findAllByPartnerId(input.getPartnerId())
				.stream().map(PartnerRepsDAO::getPartnerRepId)
				.collect(Collectors.toList());

		final List<UserDetailsDAO> userDetailsDAOs = this.userDetailsRepository.findAllByIdIn(partnerRepIds);

		return userDetailsDAOs;
	}
}
