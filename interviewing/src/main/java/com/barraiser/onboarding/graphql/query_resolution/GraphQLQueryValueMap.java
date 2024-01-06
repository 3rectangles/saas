/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql.query_resolution;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "graphql-query")
public class GraphQLQueryValueMap {
	@DynamoDBHashKey(attributeName = "query-name")
	private String queryName;

	@DynamoDBAttribute(attributeName = "query-value")
	private String value;
}
