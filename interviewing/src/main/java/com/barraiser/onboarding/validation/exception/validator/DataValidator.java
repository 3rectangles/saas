package com.barraiser.onboarding.validation.exception.validator;


import com.barraiser.common.graphql.types.ValidationResult;

public interface DataValidator<T> {

    String type();

    ValidationResult validate(T input);
}
