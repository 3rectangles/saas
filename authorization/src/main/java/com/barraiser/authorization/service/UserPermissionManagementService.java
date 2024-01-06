/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.authorization.repository.RoleRepository;
import com.barraiser.commons.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class UserPermissionManagementService {

	private UserPermissionManager userPermissionManager;
	private RoleService roleService;

	public void updatePartnerRoles(final String userId,
			final UpdatePartnerRolesRequestDTO updatePartnerRolesRequestDTO) {

		if (updatePartnerRolesRequestDTO.getDimensionForWhichRolesAreToBeRemoved() != null) {
			this.userPermissionManager.deleteUserRolesForDimension(userId,
					updatePartnerRolesRequestDTO.getDimensionForWhichRolesAreToBeRemoved(),
					updatePartnerRolesRequestDTO.getDimensionValuesForWhichRolesAreToBeRemoved(),
					updatePartnerRolesRequestDTO.getRolesToBeRemoved(),
					updatePartnerRolesRequestDTO.getUpdatedBy());
		}

		if (updatePartnerRolesRequestDTO.getDimensionForWhichRolesAreToBeAdded() != null) {
			this.userPermissionManager.addAllUserRolesForDimension(userId,
					updatePartnerRolesRequestDTO.getDimensionForWhichRolesAreToBeAdded(),
					updatePartnerRolesRequestDTO.getDimensionValuesForWhichRolesAreToBeAdded(),
					updatePartnerRolesRequestDTO.getRolesToBeAdded(),
					updatePartnerRolesRequestDTO.getUpdatedBy());
		}

	}

	public void removePartnerRoles(final String userId,
			final DeletePartnerRolesRequestDTO deletePartnerRolesRequestDTO) {

		this.userPermissionManager.deleteUserRolesForDimension(userId,
				deletePartnerRolesRequestDTO.getDimension(),
				deletePartnerRolesRequestDTO.getDimensionValues(),
				deletePartnerRolesRequestDTO.getRoleIds(),
				deletePartnerRolesRequestDTO.getUpdatedBy());
	}

	public List<UserToRoleMappingDTO> getUserRoleMapping(final GetUserRoleMappingDTO getUserRoleMappingDTO) {
		return this.userPermissionManager.getActiveUserRoleMappingsForDimension(getUserRoleMappingDTO);
	}

	public List<Role> getUserRoles(final String partnerId, final String userId) {
		final List<Role> userRoles = new ArrayList<>();
		final Map<String, Role> roleIdToRoleDAOMapping = this.roleService.getAllRoles().stream()
				.collect(Collectors.toMap(Role::getName, Function.identity()));

		this.userPermissionManager.getAllGlobalRolesAcquiredByUser(userId).stream()
				.map(r -> roleIdToRoleDAOMapping.get(r)).collect(Collectors.toCollection(() -> userRoles));

		this.userPermissionManager.getUserRolesForPartner(partnerId, userId).stream()
				.map(r -> roleIdToRoleDAOMapping.get(r)).collect(Collectors.toCollection(() -> userRoles));

		return userRoles;
	}

}
