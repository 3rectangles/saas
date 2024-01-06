/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.authorization.dal.RoleToPermissionMappingDAO;
import com.barraiser.authorization.dal.UserToRoleMappingDAO;
import com.barraiser.authorization.repository.UserToRoleMappingRepository;
import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.Resource;
import com.barraiser.commons.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.authorization.common.Constants.ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID;
import static com.barraiser.authorization.common.Constants.GLOBAL_SUPERUSER_ROLE_IDS;

@AllArgsConstructor
@Component
public class UserPermissionManager {
	private UserToRoleMappingRepository userToRoleMappingRepository;
	private PermissionManager permissionManager;

	public List<UserToRoleMappingDTO> getActiveUserRoleMappingsForDimension(
			final GetUserRoleMappingDTO getUserRoleMappingDTO) {
		return this.userToRoleMappingRepository
				.findByAuthorizationDimensionAndAuthorizationDimensionValueInAndDeletedOnIsNull(
						getUserRoleMappingDTO.getDimension(),
						List.of(getUserRoleMappingDTO.getDimensionvalue()))
				.stream()
				.map(x -> UserToRoleMappingDTO.builder()
						.userId(x.getUserId())
						.authorizationDimension(x.getAuthorizationDimension())
						.authorizationDimensionValue(x.getAuthorizationDimensionValue())
						.roleId(x.getRoleId())
						.build())
				.collect(Collectors.toList());
	}

	public void deleteUserRolesForDimension(final String userId, final Dimension dimension,
			final List<String> dimensionValues, final List<String> roleIds,
			final String updatedBy) {

		final List<UserToRoleMappingDAO> userToRoleMappingDAOS = this.getAllUserRoleMappings(userId, dimension,
				dimensionValues, roleIds);

		// Soft-delete
		this.saveAllUserRoleMappings(userToRoleMappingDAOS.stream()
				.map(x -> x.toBuilder()
						.deletedOn(Instant.now())
						.updatedBy(updatedBy)
						.build())
				.collect(Collectors.toList()));

	}

	public void addAllUserRolesForDimension(final String userId, final Dimension dimension,
			final List<String> dimensionValues, final List<String> roleIds,
			final String updatedBy) {

		final List<UserToRoleMappingDAO> userToRoleMappingDAOS = new ArrayList<>();

		for (final String roleId : roleIds) {
			for (final String dimensionValue : dimensionValues) {

				userToRoleMappingDAOS.add(
						UserToRoleMappingDAO.builder()
								.id(UUID.randomUUID().toString())
								.userId(userId)
								.roleId(roleId)
								.authorizationDimension(dimension)
								.authorizationDimensionValue(dimensionValue)
								.updatedBy(updatedBy)
								.build());
			}
		}

		this.userToRoleMappingRepository.saveAll(userToRoleMappingDAOS);
	}

	public List<UserToRoleMappingDAO> getAllUserRoleMappings(final String userId, final Dimension dimension,
			final List<String> dimensionValues, final List<String> roles) {
		return this.userToRoleMappingRepository
				.findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueInAndRoleIdInAndDeletedOnIsNull(
						userId,
						dimension,
						dimensionValues,
						roles);
	}

	public void saveAllUserRoleMappings(final List<UserToRoleMappingDAO> userToRoleMappingDAOS) {
		this.userToRoleMappingRepository.saveAll(userToRoleMappingDAOS);
	}

	public List<String> getUserRolesForPartner(final String partnerId, final String userId) {
		return this.userToRoleMappingRepository
				.findByUserIdAndAuthorizationDimensionAndAuthorizationDimensionValueAndDeletedOnIsNull(userId,
						Dimension.PARTNER,
						partnerId)
				.stream()
				.map(u -> u.getRoleId())
				.collect(Collectors.toList());
	}

	private Map<Dimension, List<DimensionRoleDTO>> getDimensionTypeToRolewiseDimensionMapping(final String userId,
			final List<Dimension> dimensions) {
		final List<UserToRoleMappingDAO> userToRoleMappingDAOS = this.userToRoleMappingRepository
				.findByUserIdAndAuthorizationDimensionInAndDeletedOnIsNull(userId, dimensions);
		final Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionMapping = new HashMap<>();

		for (UserToRoleMappingDAO userToRoleMappingDAO : userToRoleMappingDAOS) {

			final List<DimensionRoleDTO> roleDimensionListForDimensionType = dimensionTypeToRolewiseDimensionMapping
					.computeIfAbsent(
							userToRoleMappingDAO.getAuthorizationDimension(), x -> new ArrayList<>());

			final String roleId = userToRoleMappingDAO.getRoleId();
			final DimensionDTO dimensionDTO = DimensionDTO.builder()
					.dimension(userToRoleMappingDAO.getAuthorizationDimension())
					.value(userToRoleMappingDAO.getAuthorizationDimensionValue())
					.build();

			final DimensionRoleDTO dimensionRoleDTO = DimensionRoleDTO.builder()
					.roleId(roleId)
					.dimensionDTO(dimensionDTO)
					.build();

			roleDimensionListForDimensionType.add(dimensionRoleDTO);
		}

		return dimensionTypeToRolewiseDimensionMapping;
	}

	public Boolean isGlobalSuperUser(final String userId) {
		final List<String> globalRoleIdsOfUser = this.getAllGlobalRolesAcquiredByUser(userId);
		final List<String> globalSuperUserRoleIdsOfUser = globalRoleIdsOfUser.stream()
				.filter(x -> GLOBAL_SUPERUSER_ROLE_IDS.contains(x))
				.collect(Collectors.toList());

		return globalSuperUserRoleIdsOfUser.size() != 0;
	}

	// tc of *
	public List<String> getAllGlobalRolesWithRequiredPermissions(final AuthorizationInput authorizationInput) {
		final String userId = authorizationInput.getAuthenticatedUser().getUserName();
		final Action action = authorizationInput.getAction();
		final Resource resource = authorizationInput.getResource();

		final List<String> userGlobalRoleIds = this.getAllGlobalRolesAcquiredByUser(userId);
		final List<String> roleIdsWithRequiredPermissions = this.permissionManager
				.getRolesThatCanPerformActionOnResource(List.of(action),
						resource);

		final List<String> userGlobalRolesWithRequiredPermissions = userGlobalRoleIds.stream()
				.filter(u -> roleIdsWithRequiredPermissions.contains(u))
				.collect(Collectors.toList());

		return userGlobalRolesWithRequiredPermissions;
	}

	public List<String> getAllGlobalRolesAcquiredByUser(final String userId) {

		final List<String> globalRoleIds = this.permissionManager.getAllGlobalRoles()
				.stream()
				.map(RoleDAO::getId)
				.collect(Collectors.toList());

		return this.userToRoleMappingRepository.findByUserIdAndRoleIdInAndDeletedOnIsNull(userId, globalRoleIds)
				.stream()
				.map(UserToRoleMappingDAO::getRoleId)
				.collect(Collectors.toList());
	}

	/**
	 * Revisit to see if only checkingID will be sufficient
	 */
	public Boolean isSuperAdminForPartner(final String userId, final String partnerId) {
		final List<String> roleIdsOfUserForPartner = this.getUserRolesForPartner(partnerId, userId);

		final List<RoleToPermissionMappingDAO> roleToPermissionMappingDAOS = this.permissionManager
				.getRoleToPermissionMapping(roleIdsOfUserForPartner);

		for (RoleToPermissionMappingDAO roleToPermissionMappingDAO : roleToPermissionMappingDAOS) {
			if (this.isAllowedAllActionsOnAllResources(roleToPermissionMappingDAO.getPermissionId())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private boolean isAllowedAllActionsOnAllResources(final String permissionId) {
		if (ALL_ACTION_ON_ALL_RESOURCES_PERMISSION_ID.equals(permissionId)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public Map<Dimension, List<DimensionRoleDTO>> getRoleDimensionMappingForRolesWithRequiredPermissions(
			final AuthorizationInput authorizationInput, final List<Dimension> dimensions) {

		final String userId = authorizationInput.getAuthenticatedUser().getUserName();
		final Action action = authorizationInput.getAction();
		final Resource resource = authorizationInput.getResource();

		// 1.Get Role to dimension mapping
		Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionMapping = this
				.getDimensionTypeToRolewiseDimensionMapping(userId,
						dimensions);

		// 2.Get Role to permission mapping
		List<String> rolesThatCanPerformActionOnResource = this.permissionManager
				.getRolesThatCanPerformActionOnResource(List.of(action), resource);

		// Roles and dimensions that are allowed to access evaluation
		Map<Dimension, List<DimensionRoleDTO>> filteredDimensionTypeToRolewiseDimensionMapping = this
				.filterOutRolesWithInsufficientPermissions(dimensionTypeToRolewiseDimensionMapping,
						rolesThatCanPerformActionOnResource);

		return filteredDimensionTypeToRolewiseDimensionMapping;
	}

	private Map<Dimension, List<DimensionRoleDTO>> filterOutRolesWithInsufficientPermissions(
			final Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionMapping,
			final List<String> rolesThatCanPerformActionOnResource) {

		Map<Dimension, List<DimensionRoleDTO>> filteredDimensionTypeToDimensionRoleMapping = new HashMap<>();

		for (Map.Entry<Dimension, List<DimensionRoleDTO>> dimensionTypeToDimensionRole : dimensionTypeToRolewiseDimensionMapping
				.entrySet()) {

			List<DimensionRoleDTO> filteredOutDimensionRoleDTOForRolesWithRequiredPermission = dimensionTypeToDimensionRole
					.getValue().stream()
					.filter(x -> rolesThatCanPerformActionOnResource.contains(x.getRoleId()))
					.collect(Collectors.toList());

			filteredDimensionTypeToDimensionRoleMapping.put(dimensionTypeToDimensionRole.getKey(),
					filteredOutDimensionRoleDTOForRolesWithRequiredPermission);
		}

		return filteredDimensionTypeToDimensionRoleMapping;
	}

}
