package com.barraiser.common.graphqlClient;

import com.barraiser.common.entity.Entity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class GraphQLInputVariablesFactory {
    private final List<EntityToQueryInputVariablesMapper> entityToQueryInputVariablesMappers;

    public JsonNode getInputVariables(final Entity entity) {
        for(final EntityToQueryInputVariablesMapper entityToQueryInputVariablesMapper : entityToQueryInputVariablesMappers) {
            if(entityToQueryInputVariablesMapper.entityType().equals(entity.getType())) {
                return entityToQueryInputVariablesMapper.getInputVariables(entity);
            }
        }

        throw new IllegalArgumentException();
    }
}
