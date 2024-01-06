/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql.query_resolution;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GraphQLQueryFetcher {
	private final DynamoDBMapper dynamoDBMapper;

	public String fetch(String key) {
		GraphQLQueryValueMap qlQueryValueMap = new GraphQLQueryValueMap();
		qlQueryValueMap.setQueryName(key);
		GraphQLQueryValueMap res = dynamoDBMapper.load(GraphQLQueryValueMap.class, key);

		return res.getValue();
	};
}
