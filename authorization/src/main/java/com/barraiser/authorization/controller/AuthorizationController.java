/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.controller;

import com.barraiser.authorization.service.AuthorizationService;
import com.barraiser.authorization.service.FeatureAccessRegulationService;
import com.barraiser.authorization.service.RoleService;
import com.barraiser.authorization.service.UserPermissionManagementService;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.dto.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class AuthorizationController {

	private final AuthorizationService authorizationService;
	private final UserPermissionManagementService userPermissionManagementService;
	private final FeatureAccessRegulationService featureAccessRegulationService;
	private final RoleService roleService;

	@PostMapping(value = "/authorize")
	AuthorizationResult authorize(@RequestBody AuthorizationInput authorizationInput) {
		return this.authorizationService.authorize(authorizationInput);
	}

	@DeleteMapping(value = "/user/{userId}/partnerRoles")
	public void removeUserPartnerRoles(@PathVariable("userId") final String userId,
			@RequestBody DeletePartnerRolesRequestDTO deletePartnerRolesRequestDTO) {
		this.userPermissionManagementService.removePartnerRoles(userId, deletePartnerRolesRequestDTO);
	}

	@PutMapping(value = "/user/{userId}/partnerRoles")
	public void updateUserPartnerRoles(@PathVariable("userId") final String userId,
			@RequestBody UpdatePartnerRolesRequestDTO updatePartnerRolesRequestDTO) {
		this.userPermissionManagementService.updatePartnerRoles(userId, updatePartnerRolesRequestDTO);
	}

	@PostMapping(value = "/user/{userId}/accessibleFeatures")
	public List<String> getAccessibleFeaturesForUser(
			@PathVariable("userId") final String userId,
			@RequestBody final GetAccessibleFeaturesRequestDTO getAccessibleFeaturesRequestDTO) {
		return this.featureAccessRegulationService.getAccessibleFeaturesForUser(userId,
				getAccessibleFeaturesRequestDTO);
	}

	@PostMapping(value = "/dimension-wise/userRoleMappings/active")
	public List<UserToRoleMappingDTO> getActiveUserRoleMapping(
			@RequestBody final GetUserRoleMappingDTO getUserRoleMappingDTO) {
		return this.userPermissionManagementService.getUserRoleMapping(getUserRoleMappingDTO);
	}

	@GetMapping(value = "/auth/partner/{partnerId}/roles")
	public List<Role> getPartnerRoles(
			@PathVariable("partnerId") final String partnerId) {
		return this.roleService.getPartnerRoles(partnerId);
	}

	@GetMapping(value = "auth/role/{roleId}/details")
	public Role getRoleDetails(
			@PathVariable("roleId") final String roleId) {
		return this.roleService.getRoleDetails(roleId);
	}

	/**
	 * Returns all roles assumed by the user at the system level or partner level.
	 */
	@GetMapping(value = "/auth/partner/{partnerId}/user/{userId}/roles")
	public List<Role> getUserRoles(@PathVariable("partnerId") final String partnerId,
			@PathVariable("userId") final String userId) {
		return this.userPermissionManagementService.getUserRoles(partnerId, userId);
	}

}
