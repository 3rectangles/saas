/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.common.graphql.input.BarraiserPartnerUserRolesInput;
import com.barraiser.common.graphql.types.Role;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.enums.RoleType;
import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Log4j2
@Component
public class BRPartnerUserRolesDataFetcher extends AuthorizedGraphQLQuery<List<Role>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerConfigurationManager partnerConfigurationManager;
	private final ObjectMapper objectMapper;

	public BRPartnerUserRolesDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			PartnerConfigurationManager partnerConfigurationManager,
			ObjectMapper objectMapper) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.partnerConfigurationManager = partnerConfigurationManager;
		this.objectMapper = objectMapper;
	}

	@Override
	protected List<Role> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final BarraiserPartnerUserRolesInput input = this.graphQLUtil.getInput(environment,
				BarraiserPartnerUserRolesInput.class);

		if (input.getPartnerId() == null) {
			throw new IllegalArgumentException("Partner Id has to be provided to get the supported user roles");
		}

		final JsonNode userAccessConfig = this.partnerConfigurationManager.getPartnerConfiguration("user_access_config",
				input.getPartnerId(), new HashMap<>(), authenticatedUser);
		List<String> supportedUserRoles = this.objectMapper.convertValue(userAccessConfig.get("supportedPartnerRoles"),
				new TypeReference<List<String>>() {
				});

		return this.authorizationServiceFeignClient.getSupportedRolesForPartner(input.getPartnerId())
				.stream()
				.filter(r -> RoleType.PARTNER_GLOBAL.getValue().equals(r.getType()))
				.filter(r -> supportedUserRoles.contains(r.getName()))
				.map(r -> this.toRole(r))
				.collect(Collectors.toList());
	}

	private Role toRole(final com.barraiser.commons.dto.Role roleDTO) {
		return Role.builder()
				.displayName(roleDTO.getDisplayName())
				.roleType(RoleType.valueOf(roleDTO.getType()))
				.name(roleDTO.getName())
				.partnerId(roleDTO.getPartnerId())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchBRPartnerUserRoles"));
	}
}
