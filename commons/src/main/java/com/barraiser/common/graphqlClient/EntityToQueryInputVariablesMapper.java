package com.barraiser.common.graphqlClient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;

public interface EntityToQueryInputVariablesMapper {
    EntityType entityType();

    JsonNode getInputVariables(final Entity entity);
}
