/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.input.FetchValuesForFilterInput;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.common.graphql.types.FilterValue;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.FilterDAO;
import com.barraiser.onboarding.dal.FilterRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class FilterValueDataFetcher extends AuthorizedGraphQLQuery<List<FilterValue>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final FilterRepository filterRepository;
	private final FilterMapper filterMapper;
	private final ObjectMapper objectMapper;
	private final QueryDataFetcher queryDataFetcher;

	public FilterValueDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			FilterRepository filterRepository,
			FilterMapper filterMapper,
			QueryDataFetcher queryDataFetcher,
			ObjectMapper objectMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.filterRepository = filterRepository;
		this.filterMapper = filterMapper;
		this.queryDataFetcher = queryDataFetcher;
		this.objectMapper = objectMapper;
	}

	@Override
	protected List<FilterValue> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final FetchValuesForFilterInput input = this.graphQLUtil.getInput(environment, FetchValuesForFilterInput.class);

		final FilterDAO filterDAO = this.filterRepository
				.findByFilterContextAndNameAndFilterType(
						this.filterMapper.getSearchFilterContext(input.getFilterContext()),
						input.getFieldName(), ApplicableFilterType.SEARCH)
				.get();

		if (filterDAO.getQuery() == null)
			return new ArrayList<>();

		final Object queryData = this.queryDataFetcher.fetchQueryData(
				filterDAO.getQuery(),
				this.extractEntityFromFilterContextAndDependantData(
						input.getFilterContext(),
						input.getDependantData(),
						filterDAO.getEntityType()));

		return this.mapObjectToFilterValues(queryData, filterDAO.getQueryMapping());
	}

	private Entity extractEntityFromFilterContextAndDependantData(final String context, final String data,
			final EntityType entityType) {
		// TODO: Extract from dependantData

		return Entity.builder()
				.type(entityType)
				.partnerId(this.fetchPartnerIdFromFilterContext(context))
				.build();
	}

	private List<FilterValue> mapObjectToFilterValues(final Object ob, final String mapping) {
		List<FilterValue> resultList = new ArrayList<>();
		final LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>> map = (LinkedHashMap) ob;

		final List<LinkedHashMap<String, String>> valueMaps = map.get(map.keySet().iterator().next());
		if (valueMaps != null) {
			for (LinkedHashMap<String, String> valueMap : valueMaps) {
				resultList.add(FilterValue.builder()
						.id(this.fetchIdFromQuery(mapping, valueMap))
						.displayName(this.fetchNameFromQuery(mapping, valueMap))
						.build());
			}
		}

		return resultList;
	}

	@SneakyThrows
	private String fetchPartnerIdFromFilterContext(final String context) {
		final Map<String, String> map = objectMapper.readValue(context, Map.class);
		return map.get("partnerId");
	}

	@SneakyThrows
	private String fetchIdFromQuery(final String mapping, final Object payload) {
		final Map<String, String> map = objectMapper.readValue(mapping, Map.class);
		return extractValueFromPath(payload, convertStringToList(map.get("id")));
	}

	@SneakyThrows
	private String fetchNameFromQuery(final String mapping, final Object payload) {
		final Map<String, String> map = objectMapper.readValue(mapping, Map.class);
		// Checking if the mapping for displayName id direct or has longer paths.
		if (StringUtils.countMatches(map.get("displayName"), "{") == 1)
			return extractValueFromPath(payload, convertStringToList(map.get("displayName")));

		final List<String> namePaths = this.convertStringToPaths(map.get("displayName"));
		String name = "";

		for (String path : namePaths) {
			String tmp = extractValueFromPath(payload, convertStringToList(path));
			if (tmp != null)
				name += tmp + " ";
		}

		return name.trim();
	}

	private String extractValueFromPath(final Object payload, final List<String> path) {
		Object current = payload;
		for (String s : path) {
			current = ((Map<String, Object>) current).get(s);
			if (current == null)
				return null;
		}
		return current.toString();
	}

	// When the mapping is in a list of paths, example -
	// "{{userDetails,firstName},{userDetails,lastName}}"
	private List<String> convertStringToPaths(final String input) {
		String innerMapping = input.substring(2, input.length() - 2);
		String[] mappings = innerMapping.split("},\\{");
		List<String> paths = new ArrayList<>();
		for (String m : mappings) {
			paths.add("{" + m + "}");
		}
		return paths;
	}

	private List<String> convertStringToList(final String input) {
		String[] array = input.substring(1, input.length() - 1).split(",");
		List<String> list = new ArrayList<>();
		for (String value : array) {
			list.add(value.trim());
		}
		return list;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchValuesForFilter"));
	}
}
