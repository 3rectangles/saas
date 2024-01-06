/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.rest.controllers;

import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@RestController
@Log4j2
@AllArgsConstructor
public class PartnerConfigurationController {

	final PartnerConfigurationManager partnerConfigurationManager;

	// TBD: authz
	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/configuration/{partnerId}/module/{moduleName}")
	public <T> ResponseEntity<T> getPartner(@PathVariable("partnerId") final String partnerId,
			@PathVariable("moduleName") final String moduleName, final Class<T> mapperClass) {
		final T configuration = this.partnerConfigurationManager.getConfiguration(partnerId, moduleName, mapperClass);
		return ResponseEntity.ok(configuration);
	}

}
