/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.ats_integrations.dal.ATSUserRoleMappingDAO;
import com.barraiser.ats_integrations.dal.ATSUserRoleMappingRepository;
import com.barraiser.common.graphql.input.FetchATSUserRoleMappingInput;
import com.barraiser.common.graphql.types.ATSUserRoleMapping;
import com.barraiser.common.graphql.types.Role;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.commons.enums.RoleType;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class ATSUserRoleMappingDataFetcher extends AuthorizedGraphQLQuery<List<ATSUserRoleMapping>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final ATSUserRoleMappingRepository atsUserRoleMappingRepository;

	public ATSUserRoleMappingDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			ATSUserRoleMappingRepository atsUserRoleMappingRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.atsUserRoleMappingRepository = atsUserRoleMappingRepository;
	}

	@Override
	protected List<ATSUserRoleMapping> fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		// TODO: Add Authorization
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final FetchATSUserRoleMappingInput input = this.graphQLUtil.getInput(environment,
				FetchATSUserRoleMappingInput.class);

		// TODO: CHeck if we have to remve ATS Provider. For now as ATSProvider is being
		// passed as merge everything will work fine
		final List<ATSUserRoleMappingDAO> atsUserRoleMappings = this.atsUserRoleMappingRepository
				.findByAtsProviderAndPartnerId(input.getAtsProvider(), input.getPartnerId()); // TBD:

		return this.toATSUserRoleMappingsList(atsUserRoleMappings);
	}

	private List<ATSUserRoleMapping> toATSUserRoleMappingsList(final List<ATSUserRoleMappingDAO> atsUserRoleMappings) {

		List<ATSUserRoleMapping> atsUserRoles = new ArrayList<>();
		for (ATSUserRoleMappingDAO atsUserRoleMappingDAO : atsUserRoleMappings) {
			atsUserRoles.add(
					ATSUserRoleMapping.builder()
							.brUserRole(this.toRole(
									this.authorizationServiceFeignClient
											.getRoleDetailsFromId(atsUserRoleMappingDAO.getBrUserRoleId())))
							.id(atsUserRoleMappingDAO.getId())
							.atsUserRoleId(atsUserRoleMappingDAO.getAtsUserRoleId())
							.atsUserRoleName(atsUserRoleMappingDAO.getAtsUserRoleName())
							.build());
		}

		return atsUserRoles;
	}

	private Role toRole(final com.barraiser.commons.dto.Role roleDTO) {
		return Role.builder()
				.partnerId(roleDTO.getPartnerId())
				.displayName(roleDTO.getDisplayName())
				.name(roleDTO.getName())
				.roleType(RoleType.valueOf(roleDTO.getType()))
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchATSUserRoleMapping"));
	}
}
