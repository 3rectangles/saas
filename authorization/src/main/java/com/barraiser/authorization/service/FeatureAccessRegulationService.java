/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.repository.RoleToFeatureMappingRepository;
import com.barraiser.commons.dto.GetAccessibleFeaturesRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@AllArgsConstructor
@Component
public class FeatureAccessRegulationService {

	private UserPermissionManager userPermissionManager;
	private RoleToFeatureMappingRepository roleToFeatureMappingRepository;

	public List<String> getAccessibleFeaturesForUser(final String userId,
			final GetAccessibleFeaturesRequestDTO getAccessibleFeaturesRequestDTO) {

		final String partnerId = getAccessibleFeaturesRequestDTO.getPartnerId();
		final Set<String> userRoles = this.getAllUserRoles(userId, partnerId);

		return this.getAccessibleFeatures(userRoles);
	}

	private Set<String> getAllUserRoles(final String userId, final String partnerId) {

		final Set<String> userRoles = new HashSet<>();
		userRoles.addAll(this.userPermissionManager.getAllGlobalRolesAcquiredByUser(userId));
		userRoles.addAll(this.userPermissionManager.getUserRolesForPartner(partnerId, userId));

		return userRoles;
	}

	private List<String> getAccessibleFeatures(final Set<String> userRoles) {

		final List<String> accessibleFeatures = new ArrayList<>();

		this.roleToFeatureMappingRepository.findByRoleIdIn(userRoles)
				.stream()
				.forEach(x -> accessibleFeatures.addAll(x.getFeatures()));

		return accessibleFeatures;
	}

}
