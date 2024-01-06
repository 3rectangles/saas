/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.PartnerDetails;
import com.barraiser.commons.auth.UserRole;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartnerDetails implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final CompanyRepository companyRepository;
	private final Authorizer authorizer;

	@Override
	public String name() {
		return "getPartnerDetails";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		if (!authenticatedUser.getRoles().contains(UserRole.ADMIN) &&
				!authenticatedUser.getRoles().contains(UserRole.OPS)) {
			throw new AuthorizationException("User not authorised");
		}

		return DataFetcherResult.newResult().data(this.getCompanyPartnerDetails()).build();
	}

	private List<PartnerDetails> getCompanyPartnerDetails() {
		final List<PartnerCompanyDAO> partnerCompanyDAOS = this.partnerCompanyRepository.findAll();
		return partnerCompanyDAOS.stream().map(
				p -> PartnerDetails.builder()
						.partnerId(p.getId())
						.companyId(p.getCompanyId())
						.build())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
