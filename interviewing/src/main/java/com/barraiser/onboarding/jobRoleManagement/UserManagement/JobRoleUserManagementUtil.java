/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.UserManagement;

import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.DeletePartnerRolesRequestDTO;
import com.barraiser.commons.dto.GetUserRoleMappingDTO;
import com.barraiser.commons.dto.UpdatePartnerRolesRequestDTO;
import com.barraiser.commons.dto.UserToRoleMappingDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barraiser.common.constants.Constants.USER_ATS_INTEGRATION;

@AllArgsConstructor
@Component
public class JobRoleUserManagementUtil {

	private AuthorizationServiceFeignClient authorizationServiceFeignClient;

	public Map<String, List<String>> getUserIdToBrRoleIdsMapping(final String brJobRoleId) {

		final List<UserToRoleMappingDTO> userRoleMappings = this.authorizationServiceFeignClient
				.getActiveUserRoleMappingsForDimension(GetUserRoleMappingDTO.builder()
						.dimension(Dimension.JOB_ROLE_ID_VERSION)
						.dimensionvalue(brJobRoleId)
						.build());

		Map<String, List<String>> userIdToBrRoleIdsMapping = new HashMap<>();
		for (UserToRoleMappingDTO userToRoleMappingDTO : userRoleMappings) {
			final String userId = userToRoleMappingDTO.getUserId();
			final String roleId = userToRoleMappingDTO.getRoleId();

			if (!userIdToBrRoleIdsMapping.containsKey(userId)) {
				userIdToBrRoleIdsMapping.put(userId, new ArrayList<>());
			}
			userIdToBrRoleIdsMapping.get(userId).add(roleId);
		}

		return userIdToBrRoleIdsMapping;
	}

	/**
	 * This function can help us get All active HMs at a job role level
	 * or
	 * all active recruiters at a job role level.
	 *
	 * @param jobRoleId
	 * @param userRoleId
	 *            => Ex : HM or Recruiter
	 * @return
	 */
	public List<String> getAllUserWithARole(final String jobRoleId, final String userRoleId) {

		Map<String, List<String>> userIdToUserRoleIdsMapping = this.getUserIdToBrRoleIdsMapping(jobRoleId);

		final List<String> userIdsWithProvidedRoleAtJobRoleLevel = new ArrayList<>();
		for (Map.Entry<String, List<String>> userIdToBrUserRoleIdMapping : userIdToUserRoleIdsMapping.entrySet()) {

			if (userIdToBrUserRoleIdMapping.getValue().contains(userRoleId)) {
				userIdsWithProvidedRoleAtJobRoleLevel.add(userIdToBrUserRoleIdMapping.getKey());
			}
		}

		return userIdsWithProvidedRoleAtJobRoleLevel;
	}

	/**
	 * Grants user a role at the job role level
	 *
	 * @param brJobRoleId
	 * @param userId
	 */
	public void grantRole(final String brJobRoleId, final String userId, final String role) {
		this.authorizationServiceFeignClient.updatePartnerRoles(
				userId,
				UpdatePartnerRolesRequestDTO.builder()
						.dimensionForWhichRolesAreToBeRemoved(Dimension.JOB_ROLE_ID_VERSION)
						.dimensionValuesForWhichRolesAreToBeRemoved(List.of(brJobRoleId))
						.rolesToBeRemoved(List.of(role))
						.dimensionForWhichRolesAreToBeAdded(Dimension.JOB_ROLE_ID_VERSION)
						.dimensionValuesForWhichRolesAreToBeAdded(List.of(brJobRoleId))
						.rolesToBeAdded(List.of(role))
						.updatedBy(USER_ATS_INTEGRATION)
						.build());
	}

	/**
	 * Removes the role at the job role level
	 *
	 * @param brJobRoleId
	 * @param userId
	 * @param role
	 */
	public void dissociateRole(final String brJobRoleId, final String userId, final String role) {
		this.authorizationServiceFeignClient.deleteUserPartnerRoles(
				userId,
				DeletePartnerRolesRequestDTO.builder()
						.dimension(Dimension.JOB_ROLE_ID_VERSION)
						.dimensionValues(List.of(brJobRoleId))
						.roleIds(List.of(role))
						.updatedBy(USER_ATS_INTEGRATION)
						.build());
	}

}
