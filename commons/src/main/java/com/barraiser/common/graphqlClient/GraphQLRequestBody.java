package com.barraiser.common.graphqlClient;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class GraphQLRequestBody {
    private final String query;

    private final JsonNode variables;
}
