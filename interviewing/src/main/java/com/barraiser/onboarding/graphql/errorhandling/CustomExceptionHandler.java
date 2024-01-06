/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql.errorhandling;

import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.errorhandling.BarRaiserException;
import com.barraiser.onboarding.validation.exception.CustomValidationException;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CustomExceptionHandler implements DataFetcherExceptionHandler {
	@Override
	public DataFetcherExceptionHandlerResult onException(
			final DataFetcherExceptionHandlerParameters handlerParameters) {
		return DataFetcherExceptionHandlerResult.newResult()
				.error(this.buildGraphQlError(handlerParameters))
				.build();
	}

	private GraphQLError buildGraphQlError(
			final DataFetcherExceptionHandlerParameters handlerParameters) {
		final Throwable throwable = handlerParameters.getException();

		if (throwable instanceof CustomValidationException) {
			log.warn(throwable, throwable);
			final ValidationResult validationResult = ((CustomValidationException) throwable).getResult();
			return SanitizedError.builder().code(1001).validationResult(validationResult).build();
		}

		if (throwable instanceof IllegalArgumentException) {
			log.warn(throwable, throwable);
			return SanitizedError.builder().code(400).message(throwable.getMessage()).build();
		}

		if (throwable instanceof AuthenticationException
				|| throwable instanceof AuthorizationException) {
			log.warn(throwable, throwable);
			return SanitizedError.builder()
					.code(401)
					.message(
							"User is not authenticated or not authorized to perform this"
									+ " operation.")
					.build();
		}

		if (throwable instanceof BarRaiserException) {
			log.warn(throwable, throwable);
			return SanitizedError.builder()
					.code(((BarRaiserException) throwable).getErrorCode())
					.message(throwable.getMessage())
					.build();
		}
		log.error(throwable, throwable);
		return SanitizedError.builder().code(500).message(throwable.getMessage()).build();
	}
}
