/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.DTO.AuthzDTO;
import com.barraiser.commons.auth.*;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
public class GlobalRolesAuthorizerTest {

	@InjectMocks
	GlobalRolesAuthorizer globalRolesAuthorizer;

	@Mock
	UserPermissionManager userPermissionManager;

	@Test
	public void shouldGiveIsAuthorizedForGlobalSuperUser() {
		// GIVEN
		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userPermissionManager.isGlobalSuperUser(any())).thenReturn(Boolean.TRUE);

		// WHEN
		final AuthzDTO authResult = this.globalRolesAuthorizer.authorizeForGlobalRoles(authorizationInput);

		// THEN
		Assert.assertEquals(Boolean.TRUE, authResult.getIsSuperUser());
		Assert.assertEquals(Boolean.TRUE, authResult.getAuthorizationResult().getIsAuthorized());
	}

	@Test
	public void shouldGiveIsAuthorizedForUserWithPermission() {
		// GIVEN
		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userPermissionManager.isGlobalSuperUser(any())).thenReturn(Boolean.FALSE);
		when(this.userPermissionManager.getAllGlobalRolesWithRequiredPermissions(any()))
				.thenReturn(List.of(
						"CANDIDATE"));

		// WHEN
		final AuthzDTO authResult = this.globalRolesAuthorizer.authorizeForGlobalRoles(authorizationInput);

		// THEN
		Assert.assertEquals(Boolean.FALSE, authResult.getIsSuperUser());
		Assert.assertEquals(Boolean.TRUE, authResult.getAuthorizationResult().getIsAuthorized());
	}

	@Test
	public void shouldGiveIsNotAuthorizedForUserWithoutPermission() {
		// GIVEN
		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userPermissionManager.isGlobalSuperUser(any())).thenReturn(Boolean.FALSE);
		when(this.userPermissionManager.getAllGlobalRolesWithRequiredPermissions(any()))
				.thenReturn(new ArrayList<>());

		// WHEN
		final AuthzDTO authResult = this.globalRolesAuthorizer.authorizeForGlobalRoles(authorizationInput);

		// THEN
		Assert.assertEquals(Boolean.FALSE, authResult.getIsSuperUser());
		Assert.assertEquals(Boolean.FALSE, authResult.getAuthorizationResult().getIsAuthorized());
	}

}
