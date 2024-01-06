/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.SearchFilter;
import com.barraiser.commons.dto.DimensionDTO;
import com.barraiser.commons.dto.DimensionRoleDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.barraiser.authorization.common.Constants.PARTNER_SUPER_ADMIN_ROLE_ID;

/**
 * Returns authorization result for LIST action on a resource.
 */
@AllArgsConstructor
@Component
public class ListResourcePartnerRolesAuthorizer {

	private UserPermissionManager userPermissionManager;
	private AuthorizationFilterConstructor authorizationFilterConstructor;

	public AuthorizationResult performAuthorizationForPartnerRoles(final String partnerId,
			final AuthorizationInput authorizationInput) {

		AuthorizationResult partnerGlobalRolesAuthorizationResult = null;
		AuthorizationResult partnerConditionalRolesAuthorizationResult = null;

		if (this.userPermissionManager.isSuperAdminForPartner(authorizationInput.getAuthenticatedUser().getUserName(),
				partnerId)) {
			return this.constructAuthorizationResultForPartnerSuperAdmin(partnerId, authorizationInput);
		}

		partnerGlobalRolesAuthorizationResult = this
				.authorizeForPartnerGlobalRoles(authorizationInput);

		if (Boolean.FALSE.equals(partnerGlobalRolesAuthorizationResult.getIsAuthorized())) {
			return AuthorizationResult.builder()
					.isAuthorized(Boolean.FALSE)
					.build();
		}

		if (authorizationInput.getAuthorizationDimensions().size() != 0) {
			partnerConditionalRolesAuthorizationResult = this
					.authorizeForPartnerConditionalRoles(authorizationInput);
		}

		final List<SearchFilter> combinedAuthorizationFilter = this.getCombinedPartnerAuthorizationFilters(
				partnerGlobalRolesAuthorizationResult, partnerConditionalRolesAuthorizationResult);

		return AuthorizationResult.builder().isAuthorized(Boolean.TRUE)
				.authorizationFilter(SearchFilter
						.builder()
						.matchAll(combinedAuthorizationFilter)
						.build())
				.build();
	}

	private List<SearchFilter> getCombinedPartnerAuthorizationFilters(
			AuthorizationResult partnerGlobalRolesAuthorizationResult,
			AuthorizationResult partnerConditionalRolesAuthorizationResult) {
		final List<SearchFilter> combinedAuthorizationFilter = new ArrayList<>();

		if (partnerGlobalRolesAuthorizationResult != null
				&& partnerGlobalRolesAuthorizationResult.getAuthorizationFilter() != null) {
			combinedAuthorizationFilter.add(partnerGlobalRolesAuthorizationResult.getAuthorizationFilter());
		}

		if (partnerConditionalRolesAuthorizationResult != null
				&& partnerConditionalRolesAuthorizationResult.getAuthorizationFilter() != null) {
			combinedAuthorizationFilter.add(partnerConditionalRolesAuthorizationResult.getAuthorizationFilter());
		}
		return combinedAuthorizationFilter;
	}

	private AuthorizationResult authorizeForPartnerGlobalRoles(final AuthorizationInput authorizationInput) {
		Boolean isAuthorized = Boolean.FALSE;
		SearchFilter partnerDimensionAuthorizationFilters = null;

		final Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionsForPartnerGlobalRoles = this.userPermissionManager
				.getRoleDimensionMappingForRolesWithRequiredPermissions(authorizationInput, List.of(Dimension.PARTNER));

		if (dimensionTypeToRolewiseDimensionsForPartnerGlobalRoles.size() != 0) {
			isAuthorized = Boolean.TRUE;
			partnerDimensionAuthorizationFilters = this.authorizationFilterConstructor.getAuthorizationFilters(
					authorizationInput,
					dimensionTypeToRolewiseDimensionsForPartnerGlobalRoles);
		}

		return AuthorizationResult.builder()
				.isAuthorized(isAuthorized)
				.authorizationFilter(partnerDimensionAuthorizationFilters)
				.build();

	}

	private AuthorizationResult authorizeForPartnerConditionalRoles(final AuthorizationInput authorizationInput) {
		Boolean isAuthorized = Boolean.TRUE;
		SearchFilter otherDimensionAuthorizationFilters = null;

		final Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionsForPartnerConditionalRoles = this.userPermissionManager
				.getRoleDimensionMappingForRolesWithRequiredPermissions(authorizationInput,
						new ArrayList<>(authorizationInput.getAuthorizationDimensions().keySet()));

		if (dimensionTypeToRolewiseDimensionsForPartnerConditionalRoles.size() != 0) {
			otherDimensionAuthorizationFilters = this.authorizationFilterConstructor.getAuthorizationFilters(
					authorizationInput,
					dimensionTypeToRolewiseDimensionsForPartnerConditionalRoles);
		}

		return AuthorizationResult.builder()
				.isAuthorized(isAuthorized)
				.authorizationFilter(otherDimensionAuthorizationFilters)
				.build();
	}

	private AuthorizationResult constructAuthorizationResultForPartnerSuperAdmin(final String partnerId,
			final AuthorizationInput authorizationInput) {
		return AuthorizationResult.builder()
				.isAuthorized(Boolean.TRUE)
				.authorizationFilter(
						this.authorizationFilterConstructor.getAuthorizationFilters(authorizationInput,
								Map.of(Dimension.PARTNER,
										List.of(DimensionRoleDTO.builder()
												.roleId(PARTNER_SUPER_ADMIN_ROLE_ID)
												.dimensionDTO(DimensionDTO.builder()
														.dimension(Dimension.PARTNER)
														.value(partnerId)
														.build())
												.build()))))
				.build();
	}

}
