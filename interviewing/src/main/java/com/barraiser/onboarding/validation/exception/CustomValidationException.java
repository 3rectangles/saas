package com.barraiser.onboarding.validation.exception;

import com.barraiser.common.graphql.types.ValidationResult;
import lombok.Data;

@Data
public class CustomValidationException extends RuntimeException {
    ValidationResult result;

    public CustomValidationException(final String message, final ValidationResult validationResult) {
        super(message);
        this.result = validationResult;
    }
}
