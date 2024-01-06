package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QcComment {
    private String id;
    private String comment;
    private Long updatedAt;
    private String commentedById;
    private UserDetails commentedBy;
}
