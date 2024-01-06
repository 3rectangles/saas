/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.errorhandling;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

//TBD: To be moved to common
@Data
@Log4j2
public abstract class BarRaiserException extends Exception {
	private String internalErrorMessage;
	private Integer errorCode;

	public BarRaiserException(final String clientErrorMessage, final String internalErrorMessage,
			final Integer errorCode) {
		super(clientErrorMessage);
		this.internalErrorMessage = internalErrorMessage;
		this.errorCode = errorCode;
	}
}
