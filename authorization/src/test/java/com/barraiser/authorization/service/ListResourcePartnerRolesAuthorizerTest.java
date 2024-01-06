/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.repository.UserToRoleMappingRepository;
import com.barraiser.commons.auth.*;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
public class ListResourcePartnerRolesAuthorizerTest {

	@Mock
	UserPermissionManager userPermissionManager;

	@Mock
	PermissionManager permissionManager;

	@Mock
	UserToRoleMappingRepository userToRoleMappingRepository;

	@Mock
	AuthorizationFilterConstructor authorizationFilterConstructor;

	@InjectMocks
	ListResourcePartnerRolesAuthorizer listResourcePartnerRolesAuthorizer;

	@Test
	public void shouldGiveAuthorizedForSuperAdminOfPartner() {
		// GIVEN
		final String partnerId = "test_partner_company";

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userPermissionManager.isSuperAdminForPartner(any(), any())).thenReturn(Boolean.TRUE);

		// WHEN
		final AuthorizationResult authorizationResult = this.listResourcePartnerRolesAuthorizer
				.performAuthorizationForPartnerRoles(partnerId, authorizationInput);

		// THEN
		Assert.assertEquals(Boolean.TRUE, authorizationResult.getIsAuthorized());
	}

	@Test
	public void shouldGiveUnauthorizedForUserWithNoPartnerRole() {
		// GIVEN
		final String partnerId = "test_partner_company";

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userPermissionManager.isSuperAdminForPartner(any(), any())).thenReturn(Boolean.FALSE);
		when(this.userPermissionManager.getRoleDimensionMappingForRolesWithRequiredPermissions(authorizationInput,
				List.of(Dimension.PARTNER)))
						.thenReturn(new HashMap<>());
		// WHEN
		final AuthorizationResult authorizationResult = this.listResourcePartnerRolesAuthorizer
				.performAuthorizationForPartnerRoles(partnerId, authorizationInput);

		// THEN
		Assert.assertEquals(Boolean.FALSE, authorizationResult.getIsAuthorized());
	}

}
