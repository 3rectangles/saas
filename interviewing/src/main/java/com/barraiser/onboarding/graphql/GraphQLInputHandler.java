package com.barraiser.onboarding.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class GraphQLInputHandler {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T getArgument(
            final DataFetchingEnvironment environment, final String param, final Class<T> c) {
        final Object input = environment.getArgument(param);
        return (T) this.objectMapper.readValue(this.objectMapper.writeValueAsString(input), c);
    }

    public <T> T getInput(final DataFetchingEnvironment environment, final Class<T> c) {
        return this.getArgument(environment, "input", c);
    }
}
