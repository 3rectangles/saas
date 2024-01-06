/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.commons.auth.*;
import com.barraiser.commons.dto.DimensionDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationFilterConstructorTest {

	@Mock
	UserPermissionManager userPermissionManager;

	@Mock
	PermissionManager permissionManager;

	@InjectMocks
	AuthorizationFilterConstructor authorizationFilterConstructor;

	// not sourcing partner
	@Test
	public void testScenario1() {

		// GIVEN
		when(this.permissionManager.isSourcingPartner(any()))
				.thenReturn(Boolean.FALSE);

		final Map<String, DimensionDTO> roleToDimensionMappings = Map.of(
				"role_id_1", DimensionDTO
						.builder()
						.dimension(Dimension.JOB_ROLE_ID_VERSION)
						.value("jr1")
						.build());

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		// TBD
		// // WHEN
		// List<SearchFilter> searchFilterList = this.authorizationFilterConstructor
		// .getAuthorizationFilters(authorizationInput, roleToDimensionMappings);
		//
		// log.info("" + searchFilterList.get(0).toString());
		// // THEN
		// Assert.assertEquals(1, searchFilterList.size());
	}

	// when sourcing partner
	@Test
	public void test2() {

		// GIVEN

		final Map<String, DimensionDTO> roleToDimensionMappings = Map.of(
				"role_id_1", DimensionDTO
						.builder()
						.dimension(Dimension.JOB_ROLE_ID_VERSION)
						.value("jr1")
						.build());

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.email("testuser@sourcer.com")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.permissionManager.isSourcingPartner(any()))
				.thenReturn(Boolean.TRUE);

		// TBD
		// // WHEN
		// List<SearchFilter> searchFilterList = this.authorizationFilterConstructor
		// .getAuthorizationFilters(authorizationInput, roleToDimensionMappings);
		//
		// Assert.assertEquals(1, searchFilterList.size());
	}

}
