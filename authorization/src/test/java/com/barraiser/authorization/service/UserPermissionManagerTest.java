/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.authorization.dal.RoleToPermissionMappingDAO;
import com.barraiser.authorization.dal.UserToRoleMappingDAO;
import com.barraiser.authorization.repository.UserToRoleMappingRepository;
import com.barraiser.commons.auth.*;
import com.barraiser.commons.dto.DimensionRoleDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.barraiser.authorization.common.Constants.ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
public class UserPermissionManagerTest {

	@Mock
	PermissionManager permissionManager;

	@Mock
	UserToRoleMappingRepository userToRoleMappingRepository;

	@InjectMocks
	UserPermissionManager userPermissionManager;

	@Test
	public void testGetAllGlobalRolesWithPermission() {

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

		when(this.permissionManager.getAllGlobalRoles())
				.thenReturn(
						List.of(RoleDAO.builder()
								.id("CANDIDATE")
								.build(),
								RoleDAO.builder()
										.id("EXPERT")
										.build(),
								RoleDAO.builder()
										.id("ADMIN")
										.build()));

		when(this.userToRoleMappingRepository.findByUserIdAndRoleIdInAndDeletedOnIsNull(any(), any()))
				.thenReturn(
						List.of(UserToRoleMappingDAO.builder()
								.userId("test_user_id")
								.roleId("CANDIDATE")
								.build(),
								UserToRoleMappingDAO.builder()
										.userId("test_user_id")
										.roleId("EXPERT")
										.build()));

		when(this.permissionManager.getRolesThatCanPerformActionOnResource(any(), any()))
				.thenReturn(
						List.of("roleId1", "roleId2", "CANDIDATE"));

		// WHEN
		List<String> globalRolesWithPermission = this.userPermissionManager
				.getAllGlobalRolesWithRequiredPermissions(authorizationInput);

		// THEN
		Assert.assertEquals(1, globalRolesWithPermission.size());
		Assert.assertEquals("CANDIDATE", globalRolesWithPermission.get(0));
	}

	@Test
	public void shouldBeSuperAdminForPartner() {

		// GIVEN
		final String userId = "test_user";
		final String partnerId = "test_partner_company";

		when(this.userToRoleMappingRepository
				.findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueAndDeletedOnIsNull(any(), any(),
						any()))
								.thenReturn(
										List.of(
												UserToRoleMappingDAO.builder()
														.roleId("super_admin_role")
														.build()));

		when(this.permissionManager.getRoleToPermissionMapping(List.of("super_admin_role")))
				.thenReturn(List.of(
						RoleToPermissionMappingDAO.builder()
								.roleId("super_admin_role")
								.permissionId(ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID)
								.build()));

		// WHEN
		Boolean isSuperAdminForPartner = this.userPermissionManager.isSuperAdminForPartner(userId, partnerId);

		// THEN
		Assert.assertEquals(Boolean.TRUE, isSuperAdminForPartner);
	}

	@Test
	public void shouldNotBeSuperAdminForPartner() {
		// GIVEN
		final String userId = "test_user";
		final String partnerId = "test_partner_company";

		when(this.userToRoleMappingRepository
				.findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueAndDeletedOnIsNull(any(), any(),
						any()))
								.thenReturn(
										List.of(
												UserToRoleMappingDAO.builder()
														.roleId("randomRole2")
														.build()));

		when(this.permissionManager.getRoleToPermissionMapping(List.of("super_admin_role")))
				.thenReturn(List.of(
						RoleToPermissionMappingDAO.builder()
								.roleId("super_admin_role")
								.permissionId(ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID)
								.build(),
						RoleToPermissionMappingDAO.builder()
								.roleId("randomRole2")
								.permissionId("randomPermissionId")
								.build()));

		// WHEN
		Boolean isSuperAdminForPartner = this.userPermissionManager.isSuperAdminForPartner(userId, partnerId);

		// THEN
		Assert.assertEquals(Boolean.FALSE, isSuperAdminForPartner);
	}

	@Test
	public void shouldFilterOutRolesWithInsufficientPermission() {

		// GIVEN
		final String userId = "test_user_id";

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userToRoleMappingRepository.findByUserIdAndAuthorizationDimensionInAndDeletedOnIsNull(any(), any()))
				.thenReturn(List.of(
						UserToRoleMappingDAO.builder()
								.userId(userId)
								.roleId("roleid1")
								.authorizationDimension(Dimension.JOB_ROLE_ID_VERSION)
								.authorizationDimensionValue("jobroleid1")
								.build(),
						UserToRoleMappingDAO.builder()
								.userId(userId)
								.authorizationDimension(Dimension.JOB_ROLE_ID_VERSION)
								.authorizationDimensionValue("jobroleid2")
								.roleId("roleid2")
								.build()));

		when(this.permissionManager.getRolesThatCanPerformActionOnResource(any(), any()))
				.thenReturn(List.of("roleid2"));

		// WHEN
		final Map<Dimension, List<DimensionRoleDTO>> roleDimensionMapping = this.userPermissionManager
				.getRoleDimensionMappingForRolesWithRequiredPermissions(authorizationInput, List.of(Dimension.PARTNER));

		// THEN
		Assert.assertNotNull(roleDimensionMapping);
		Assert.assertEquals(1, roleDimensionMapping.size());
		Assert.assertEquals(1, roleDimensionMapping.get(Dimension.JOB_ROLE_ID_VERSION).size());
		Assert.assertEquals("roleid2", roleDimensionMapping.get(Dimension.JOB_ROLE_ID_VERSION).get(0).getRoleId());

	}

	@Test
	public void shouldFilterOutRolesWithInsufficientPermissionAcrossMultipleDimensions() {

		// GIVEN
		final String userId = "test_user_id";

		final AuthorizationInput authorizationInput = AuthorizationInput.builder()
				.authenticatedUser(AuthenticatedUser.builder()
						.userName("test_user")
						.build())
				.authorizationDimensions(Map.of(Dimension.JOB_ROLE_ID_VERSION, ""))
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(Map.of("partnerId", "test_partner"))
				.build();

		when(this.userToRoleMappingRepository.findByUserIdAndAuthorizationDimensionInAndDeletedOnIsNull(any(), any()))
				.thenReturn(List.of(
						UserToRoleMappingDAO.builder()
								.userId(userId)
								.roleId("roleid1")
								.authorizationDimension(Dimension.JOB_ROLE_ID_VERSION)
								.authorizationDimensionValue("jobroleid1")
								.build(),
						UserToRoleMappingDAO.builder()
								.userId(userId)
								.authorizationDimension(Dimension.PARTNER)
								.authorizationDimensionValue("partner1")
								.roleId("roleid2")
								.build(),
						UserToRoleMappingDAO.builder()
								.userId(userId)
								.roleId("roleid3")
								.authorizationDimension(Dimension.JOB_ROLE_ID_VERSION)
								.authorizationDimensionValue("jobroleid1")
								.build()));

		when(this.permissionManager.getRolesThatCanPerformActionOnResource(any(), any()))
				.thenReturn(List.of("roleid2", "roleid3"));

		// WHEN
		final Map<Dimension, List<DimensionRoleDTO>> roleDimensionMapping = this.userPermissionManager
				.getRoleDimensionMappingForRolesWithRequiredPermissions(authorizationInput, List.of(Dimension.PARTNER));

		// THEN
		Assert.assertNotNull(roleDimensionMapping);
		Assert.assertEquals(2, roleDimensionMapping.size());
		Assert.assertEquals(1, roleDimensionMapping.get(Dimension.JOB_ROLE_ID_VERSION).size());
		Assert.assertEquals(1, roleDimensionMapping.get(Dimension.PARTNER).size());
		Assert.assertEquals("roleid3", roleDimensionMapping.get(Dimension.JOB_ROLE_ID_VERSION).get(0).getRoleId());
		Assert.assertEquals("roleid2", roleDimensionMapping.get(Dimension.PARTNER).get(0).getRoleId());

	}
}
