/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.endpoint;

import com.barraiser.ats_integrations.services.ATSCredentialManagementService;
import com.barraiser.commons.dto.ats.ATSSecretDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class ATSCredentialManagementController {

	final static String SERVICE_CONTEXT_PATH = "/ats";
	private final ATSCredentialManagementService atsCredentialManagementService;

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/ats-secrets")
	ResponseEntity<List<ATSSecretDTO>> getATSSecrets(
			@PathVariable("partnerId") final String partnerId) {

		return ResponseEntity.ok().body(this.atsCredentialManagementService.getATSSecretsForPartner(partnerId));
	}
}
