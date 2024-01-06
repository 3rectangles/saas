package com.barraiser.onboarding.files.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@DynamoDBTable(tableName = "documents")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentsDAO extends BaseModel {
    @DynamoDBHashKey(attributeName = "userId")
    private String userId;
    @DynamoDBRangeKey(attributeName = "documentId")
    private String documentId;
    private String name;
    private String s3Url;
}
