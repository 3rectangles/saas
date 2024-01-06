package com.barraiser.onboarding.graphql.errorhandling;

import com.barraiser.common.graphql.types.ValidationResult;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class SanitizedError implements GraphQLError {
    private int code;
    private String message;
    private ValidationResult validationResult;
    private List<SourceLocation> locations;
    private ErrorClassification errorType;
}
