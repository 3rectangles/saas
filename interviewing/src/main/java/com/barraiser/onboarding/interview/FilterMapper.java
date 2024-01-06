/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.ApplicableFilter;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.commons.auth.FilterOperator;
import com.barraiser.onboarding.dal.FilterDAO;
import com.barraiser.onboarding.dal.FilterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilterMapper {
	FilterRepository filterRepository;
	ObjectMapper objectMapper;

	public ApplicableFilter toApplicableFilter(final FilterDAO filter) {
		List<FilterOperator> operators = new ArrayList<>();
		if (filter.getFilterType().equals(ApplicableFilterType.SEARCH)) {
			operators = filter.getOperationsPossible().stream().map(FilterOperator::valueOf)
					.collect(Collectors.toList());
		}

		return ApplicableFilter.builder()
				.dependantFields(filter.getDependantFields())
				.displayName(filter.getDisplayName())
				.fieldName(filter.getName())
				.fieldType(filter.getFieldType())
				.operationsPossible(operators)
				.filterType(filter.getFilterType())
				.defaultValue(filter.getDefaultValue())
				.build();
	}

	@SneakyThrows
	public String fetchContextFromFilterContext(final String context) {
		final Map<String, String> map = objectMapper.readValue(context, Map.class);
		return map.get("context");
	}

	@SneakyThrows
	public String fetchModelFromFilterContext(final String context) {
		final Map<String, String> map = objectMapper.readValue(context, Map.class);
		return map.get("model");
	}

	public String getSearchFilterContext(String filterContext) {
		return "{\"context\":\"" + this.fetchContextFromFilterContext(filterContext) +
				"\",\"model\":\"" + this.fetchModelFromFilterContext(filterContext) +
				"\"}";
	}
}
