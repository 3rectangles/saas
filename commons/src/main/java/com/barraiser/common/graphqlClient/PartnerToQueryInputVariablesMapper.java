package com.barraiser.common.graphqlClient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.input.PartnerInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class PartnerToQueryInputVariablesMapper implements EntityToQueryInputVariablesMapper {
    private final ObjectMapper objectMapper;

    @Override
    public EntityType entityType() {
        return EntityType.PARTNER;
    }

    @Override
    public JsonNode getInputVariables(final Entity entity) {
        final Map<String, Object> variables = new HashedMap();
        variables.put("input", PartnerInput.builder()
            .partnerId(entity.getId())
            .build());
        return this.objectMapper.valueToTree(variables);
    }
}
