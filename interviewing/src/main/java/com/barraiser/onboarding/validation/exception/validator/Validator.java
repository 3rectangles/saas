/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.validation.exception.validator;

import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.onboarding.validation.exception.CustomValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class Validator {

	private final List<DataValidator> dataValidatorList;

	public void validate(final Object input) {

		this.dataValidatorList.stream()
				.forEach(v -> {
					Class inputObjectClass = input.getClass();
					Type dataValidatorArgumentType = ((ParameterizedType) v.getClass().getGenericInterfaces()[0])
							.getActualTypeArguments()[0];
					if (inputObjectClass.equals(dataValidatorArgumentType)) {
						this.validateInput(input, v);
					}
				});

	}

	public void validateInput(final Object input, final DataValidator dataValidator) {
		ValidationResult validationResult = dataValidator.validate(input);

		if (!(validationResult.getFieldErrors() == null || validationResult.getFieldErrors().isEmpty())
				|| !(validationResult.getOverallErrors() == null || validationResult.getOverallErrors().isEmpty())) {
			throw new CustomValidationException("", validationResult);
		}

	}
}
