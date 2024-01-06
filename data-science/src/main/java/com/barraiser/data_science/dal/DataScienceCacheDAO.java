/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "datascience-cache")
public class DataScienceCacheDAO {
	@DynamoDBHashKey(attributeName = "request_hash")
	private String requestHash;

	@DynamoDBAttribute(attributeName = "request_body")
	private String requestBody;

	@DynamoDBAttribute(attributeName = "response_body")
	private String responseBody;
}
