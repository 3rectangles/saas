/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.merge.DTO.MergeAccountTokenResponseDTO;
import com.barraiser.ats_integrations.merge.DTO.MergeLinkTokenRequestDTO;
import com.barraiser.ats_integrations.merge.DTO.MergeLinkTokenResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "merge-authentication-client", url = MergeAuthenticationClient.MERGE_AUTHENTICATION_BASE_URL)
public interface MergeAuthenticationClient {
	String MERGE_AUTHENTICATION_BASE_URL = "https://api.merge.dev/api/integrations";
	String AUTHORIZATION = "Authorization";

	@PostMapping(value = "/create-link-token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MergeLinkTokenResponseDTO> createLinkToken(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestBody final MergeLinkTokenRequestDTO requestDTO);

	@GetMapping(value = "/account-token/{publicToken}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MergeAccountTokenResponseDTO> getAccountToken(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@PathVariable("publicToken") final String publicToken);
}
