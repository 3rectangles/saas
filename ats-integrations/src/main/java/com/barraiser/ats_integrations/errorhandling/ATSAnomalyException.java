/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.errorhandling;

import com.barraiser.onboarding.errorhandling.BarRaiserException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
public class ATSAnomalyException extends BarRaiserException {

	public ATSAnomalyException(final String clientErrorMessage, final String internalErrorMessage,
			final Integer errorCode) {
		super(clientErrorMessage, internalErrorMessage, errorCode);
	}

}
