package com.barraiser.communication.automation;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.common.entity.Entity;
import com.barraiser.common.graphqlClient.GraphQLClient;
import com.barraiser.common.graphqlClient.GraphQLInputVariablesFactory;
import com.barraiser.common.graphqlClient.GraphQLRequestBody;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class QueryDataFetcher {
    private final GraphQLClient graphQLClient;
    private final GraphQLInputVariablesFactory graphQLInputVariablesFactory;
    private final AWSSecretsManager awsSecretsManager;
    private final CommunicationStaticAppConfig communicationStaticAppConfig;

    private String apiKey;

    public Object fetchQueryData(final String query, final Entity entity) {
        final GraphQLRequestBody requestBody = GraphQLRequestBody.builder()
            .query(query)
            .variables(graphQLInputVariablesFactory.getInputVariables(entity))
            .build();

        final Object response = this.graphQLClient.getGraphQLData(requestBody, this.apiKey);
        return ((Map<String, Object>) response).get("data");
    }

    public Object getObjectFromPath(final Object o, List<String> path) {
        Object current = o;
        for(final String key: path) {
            if(current instanceof ArrayList) {
                current = ((ArrayList<?>) current).get(Integer.parseInt(key));
            }
            else {
                current = ((Map<?, ?>) current).get(key);
            }
        }
        return current;
    }

    @PostConstruct
    private void init() {
        this.apiKey = this.awsSecretsManager.getSecretValue(
            new GetSecretValueRequest().withSecretId(this.communicationStaticAppConfig.getCommunicationServiceApiKeyName())
        ).getSecretString();
    }
}
