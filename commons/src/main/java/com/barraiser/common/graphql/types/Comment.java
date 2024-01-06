package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Comment {
    private String comment;
    private String createdOn;
    private String updatedOn;
    private String author;
    private String entityId;
    private String entityType;
}
