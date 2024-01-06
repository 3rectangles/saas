/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphqlClient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.input.GetLocationsInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class LocationToQueryInputVariablesMapper implements EntityToQueryInputVariablesMapper {
	private final ObjectMapper objectMapper;

	@Override
	public EntityType entityType() {
		return EntityType.LOCATION;
	}

	@Override
	public JsonNode getInputVariables(Entity entity) {
		final Map<String, Object> variables = new HashMap<>();

		variables
				.put(
						"input",
						GetLocationsInput
								.builder()
								.partnerId(entity.getPartnerId())
								.build());

		return this.objectMapper
				.valueToTree(variables);
	}
}
