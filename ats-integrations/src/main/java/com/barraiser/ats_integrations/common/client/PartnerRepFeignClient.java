/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.common.graphql.types.PartnerRepDetails;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "partner-rep-feign-client", url = "http://localhost:5000")
public interface PartnerRepFeignClient {

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partnerRep")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> addPartnerRep(@RequestBody PartnerAccessInput addPartnerRepRequest);

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/getPartnerRepDetails/{partnerRepEmailId}/userEmail")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<PartnerRepDetails> getPartnerRepDetails(
			@PathVariable("partnerRepEmailId") final String partnerRepEmailId);

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/addInterviewer")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> addInterviewer(@RequestBody PartnerAccessInput addPartnerRepRequest);

}
