package com.barraiser.onboarding.communicationUtils;

import com.barraiser.onboarding.graphql.GraphQLProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.FieldDefinition;
import graphql.language.NonNullType;
import graphql.language.TypeName;
import graphql.schema.GraphQLObjectType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Log4j2
public class QueryGenerator {
    public String generate(final String queryName, final List<List<String>> variables) throws JsonProcessingException {
        final List<List<String>> inputTypes = this.getInputNameAndTypeForQuery(queryName);
        String outerInputString = "";
        String innerInputString = "";
        for(int i = 0; i < inputTypes.size(); ++i) {
            outerInputString += String.format("$%s: %s", inputTypes.get(i).get(0), inputTypes.get(i).get(1));
            innerInputString += String.format("%s: $%s", inputTypes.get(i).get(0), inputTypes.get(i).get(0));
            if(i < inputTypes.size() - 1) {
                outerInputString += ",";
                innerInputString += ",";
            }
        }
        if(!inputTypes.isEmpty()) {
            outerInputString = "(" + outerInputString + ")";
            innerInputString = "(" + innerInputString + ")";
        }
        final String queryBody = this.getQueryBody(variables);
        final String query = String.format("query %s%s { %s%s %s }", queryName, outerInputString, queryName, innerInputString, queryBody);
        return query;
    }

    private String getQueryBody(final List<List<String>> variables) throws JsonProcessingException {
        HashMap<String, Object> root = new HashMap<String, Object>();

        for(final List<String> variable: variables) {
            Map<String, Object> temp_map = root;
            String value = "";
            for(int i = 0; i < variable.size(); ++i) {
                final String path = variable.get(i);
                if(i == variable.size() - 1) {
                    temp_map.put(path, value);
                }
                else {
                    Map<String, Object> temp_map1 = (HashMap<String, Object>) temp_map.get(path);
                    if (temp_map1 == null) {
                        temp_map1 = new HashMap<String, Object>();
                        temp_map.put(path, temp_map1);
                    }
                    temp_map = temp_map1;
                }
            }
        }

        String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        return json.replace("\"","").replace(":","").replace(",","");
    }

    private List<List<String>> getInputNameAndTypeForQuery(final String queryName) {
        final GraphQLObjectType queryType = GraphQLProvider.graphQLSchema.getQueryType();

        for(final FieldDefinition queryFieldDefinition : queryType.getDefinition().getFieldDefinitions()) {
            if(queryFieldDefinition.getName().equals(queryName)) {
                return queryFieldDefinition.getInputValueDefinitions().stream()
                    .map(i -> {
                        final boolean isMandatory = i.getType() instanceof NonNullType;
                        return List.of(i.getName(),
                            isMandatory ?
                                ((TypeName) ((NonNullType) i.getType()).getType()).getName() + "!" :
                                ((TypeName) i.getType()).getName()
                        );
                    })
                    .collect(Collectors.toList());
            }
        }
        throw new IllegalArgumentException(String.format("query %s not found in schema", queryName));
    }
}
