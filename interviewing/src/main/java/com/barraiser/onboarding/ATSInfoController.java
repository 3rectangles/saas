/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding;

import com.barraiser.commons.dto.ats.ATSIntegrationDTO;
import com.barraiser.onboarding.ats_integrations.ATSConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class ATSInfoController {

	final static String SERVICE_CONTEXT_PATH = "/interviewing";
	final ATSConfigService atsConfigService;

	/**
	 * Returns all supported integrations partner wise.
	 *
	 * @return
	 */
	@GetMapping(value = SERVICE_CONTEXT_PATH + "/integrations/supported")
	ResponseEntity<List<ATSIntegrationDTO>> getAllSupportedIntegrations() {
		return ResponseEntity.ok().body(this.atsConfigService.getAllSupportedIntegrations());
	}
}
