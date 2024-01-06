/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphqlClient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmptyQueryInputVariablesMapper implements EntityToQueryInputVariablesMapper {
	private final ObjectMapper objectMapper;

	@Override
	public EntityType entityType() {
		return EntityType.EMPTY;
	}

	@Override
	public JsonNode getInputVariables(final Entity entity) {
		final Map<String, Object> variables = new HashedMap();

		return this.objectMapper.valueToTree(variables);
	}
}
