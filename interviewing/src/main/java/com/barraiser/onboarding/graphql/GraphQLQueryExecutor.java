package com.barraiser.onboarding.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class GraphQLQueryExecutor {
    private final DataLoaderRegistryFactory dataLoaderRegistryFactory;

    public ExecutionResult execute(final String query, final Map<String, Object> variables, final GraphQLContext context) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
            .context(context)
            .query(query)
            .dataLoaderRegistry(this.dataLoaderRegistryFactory.newDataLoaderRegistry())
            .build();

        if (variables != null) {
            executionInput = executionInput.transform(x -> x.variables(variables).build());
        }

        return GraphQLProvider.graphQL.execute(executionInput);
    }
}
