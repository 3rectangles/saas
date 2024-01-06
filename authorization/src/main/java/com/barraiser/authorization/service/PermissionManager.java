/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.dal.PermissionDAO;
import com.barraiser.authorization.dal.RoleDAO;
import com.barraiser.authorization.dal.RoleToPermissionMappingDAO;
import com.barraiser.authorization.repository.PermissionRepository;
import com.barraiser.authorization.repository.RoleRepository;
import com.barraiser.authorization.repository.RoleToPermissionMappingRepository;
import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.Resource;
import com.barraiser.commons.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.barraiser.authorization.common.Constants.SOURCING_PARTNER_ROLE_ID;

@AllArgsConstructor
@Component
public class PermissionManager {
	private RoleRepository roleRepository;
	private PermissionRepository permissionRepository;
	private RoleToPermissionMappingRepository roleToPermissionMappingRepository;

	public List<RoleDAO> getAllGlobalRoles() {
		return this.roleRepository.findByType(RoleType.GLOBAL);
	}

	public List<PermissionDAO> getPermissions(final List<Action> actions, final Resource resource) {
		return this.permissionRepository.findByActionInAndResource(actions, resource);
	}

	public List<String> getRolesThatCanPerformActionOnResource(final List<Action> actions, final Resource resource) {

		final List<PermissionDAO> permissions = this.getPermissions(actions, resource);

		final List<RoleToPermissionMappingDAO> rolesToPermissionDAOs = this.roleToPermissionMappingRepository
				.findByPermissionIdIn(permissions.stream().map(p -> p.getId()).collect(Collectors.toList()));

		return rolesToPermissionDAOs.stream()
				.map(x -> x.getRoleId())
				.collect(Collectors.toList());
	}

	public List<RoleToPermissionMappingDAO> getRoleToPermissionMapping(final List<String> roleIds) {
		return this.roleToPermissionMappingRepository.findByRoleIdIn(roleIds);
	}

	public Boolean isSourcingPartner(final String roleId) {
		return SOURCING_PARTNER_ROLE_ID.equals(roleId);
	}

}
