/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.commons.auth.*;
import com.barraiser.commons.dto.DimensionDTO;
import com.barraiser.commons.dto.DimensionRoleDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class AuthorizationFilterConstructor {

	private PermissionManager permissionManager;

	public SearchFilter getAuthorizationFilters(final AuthorizationInput authorizationInput,
			final Map<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionsMappings) {

		final List<SearchFilter> specialConditionFilers = new ArrayList<>();
		final Map<Dimension, SearchFilter> perDimensionTypeFilter = new HashMap<>();

		for (Map.Entry<Dimension, List<DimensionRoleDTO>> dimensionTypeToRolewiseDimensionsMapping : dimensionTypeToRolewiseDimensionsMappings
				.entrySet()) {

			final Dimension dimension = dimensionTypeToRolewiseDimensionsMapping.getKey();
			final List<SearchFilter> dimensionLevelAuthorizationFilters = new ArrayList<>();

			for (DimensionRoleDTO dimensionRoleDTO : dimensionTypeToRolewiseDimensionsMapping.getValue()) {

				if (this.permissionManager.isSourcingPartner(dimensionRoleDTO.getRoleId())) {
					if (Resource.EVALUATION.equals(authorizationInput.getResource())) {
						specialConditionFilers.add(this.getAuthorizationFilterForSourcingPartner(authorizationInput,
								dimensionRoleDTO.getDimensionDTO()));
					}
				}

				dimensionLevelAuthorizationFilters
						.add(this.getAuthorizationDimensionFilter(dimensionRoleDTO.getDimensionDTO()));

			}

			if (dimensionLevelAuthorizationFilters.size() != 0) {
				perDimensionTypeFilter.put(dimension,
						SearchFilter.builder().matchAnyOf(dimensionLevelAuthorizationFilters).build());
			}
		}

		return this.combineFilters(specialConditionFilers, perDimensionTypeFilter);
	}

	private SearchFilter combineFilters(final List<SearchFilter> specialCaseAuthorizationFilters,
			final Map<Dimension, SearchFilter> perDimensionTypeAuthorizationFilters) {

		final List<SearchFilter> combinedSearchFilters = new ArrayList<>();

		if (specialCaseAuthorizationFilters.size() != 0) {
			combinedSearchFilters.add(
					SearchFilter.builder()
							.matchAnyOf(specialCaseAuthorizationFilters)
							.build());
		}

		combinedSearchFilters.add(SearchFilter.builder()
				.matchAnyOf(new ArrayList<>(perDimensionTypeAuthorizationFilters.values()))
				.build());

		return SearchFilter.builder()
				.matchAll(combinedSearchFilters)
				.build();
	}

	private SearchFilter getAuthorizationDimensionFilter(final DimensionDTO dimensionDTO) {

		return SearchFilter.builder()
				.field(dimensionDTO.getDimension().getValue())
				.operator(FilterOperator.EQUALS)
				.value(dimensionDTO.getValue())
				.build();
	}

	private SearchFilter getAuthorizationFilterForSourcingPartner(final AuthorizationInput authorizationInput,
			final DimensionDTO dimensionDTO) {
		/**
		 * This filter will ensure a sourcing partner can see only those candidates that
		 * they have added.
		 */
		final SearchFilter evaluationSourcedFromSameCompanyFilter = SearchFilter.builder()
				.field("pocEmail")
				.operator(FilterOperator.LIKE)
				.value(authorizationInput.getAuthenticatedUser().getEmail())
				.build();
		final SearchFilter dimensionFilter = SearchFilter.builder()
				.field(dimensionDTO.getDimension().getValue())
				.operator(FilterOperator.EQUALS)
				.value(dimensionDTO.getValue())
				.build();

		return SearchFilter.builder()
				.matchAll(
						List.of(
								evaluationSourcedFromSameCompanyFilter,
								dimensionFilter))
				.build();
	}

}
