package com.barraiser.common.graphql.types;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class FieldValidationResult {

    private String resourceIdentifier;

    private String message;

    private String fieldTag;
}

