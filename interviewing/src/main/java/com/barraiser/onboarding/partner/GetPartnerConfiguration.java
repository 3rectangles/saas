/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.GetPartnerConfigurationInput;
import com.barraiser.common.graphql.types.PartnerConfiguration;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerConfigurationDAO;
import com.barraiser.onboarding.dal.PartnerConfigurationRepository;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.commons.auth.UserRole;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartnerConfiguration implements GraphQLQuery {
	private final GraphQLUtil graphQLUtil;
	private final PartnerConfigurationRepository partnerConfigurationRepository;

	@Override
	public String name() {
		return "getPartnerConfiguration";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		if (!authenticatedUser.getRoles().contains(UserRole.ADMIN)) {
			throw new AuthorizationException("User not authorised");
		}

		final GetPartnerConfigurationInput input = this.graphQLUtil.getInput(environment,
				GetPartnerConfigurationInput.class);

		final String partnerId = input.getPartnerId();

		final Optional<PartnerConfigurationDAO> partnerConfigurationDAO = this.partnerConfigurationRepository
				.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId);

		if (!partnerConfigurationDAO.isPresent()) {
			throw new IllegalArgumentException("Partner Configuration is not present");
		}

		String config = partnerConfigurationDAO.get().getConfig().toString();

		final PartnerConfiguration partnerConfiguration = PartnerConfiguration.builder()
				.data(config)
				.build();

		return DataFetcherResult.newResult().data(partnerConfiguration).build();
	}

}
