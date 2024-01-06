/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.errorhandling.exception;

import com.barraiser.onboarding.errorhandling.BarRaiserException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class IllegalOperationException extends BarRaiserException {

	public IllegalOperationException(final String clientErrorMessage, final String internalErrorMessage,
			final Integer errorCode) {
		super(clientErrorMessage, internalErrorMessage, errorCode);
	}

}
