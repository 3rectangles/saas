/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.dto.*;
import com.barraiser.commons.dto.User.role.UpdateUserRoleMappingInput;
import com.barraiser.commons.dto.ats.ATSSecretDTO;
import com.barraiser.onboarding.ats_integrations.dto.ATSInterviewStructureDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ats-service-client", url = "http://localhost:5000")
public interface ATSServiceClient {
	String SERVICE_CONTEXT_PATH = "/ats";

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/ats-job-role")
	ResponseEntity<ATSJobRoleDTO> getAtsJobRoleIdFromBrJobRoleId(
			@RequestParam("br-job-role-id") final String brJobRoleId);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/ats-interview-structure")
	ResponseEntity<ATSInterviewStructureDTO> getAtsInterviewStructureId(
			@RequestParam("br-interview-structure-id") final String brInterviewStructureId);

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/job-role-mapping")
	ResponseEntity<Void> updateJobRoleMappings(
			@RequestBody final UpdateAtsJobRoleMappingDTO request);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/job-role")
	ResponseEntity<ATSJobRoleMappingsDTO> getATSJobRoleMappings(@RequestParam("partnerId") final String partnerId);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/user-role")
	ResponseEntity<ATSUserRoleMappingsDTO> getATSUserRoleMappings(@RequestParam("partnerId") final String partnerId);

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/partner-rep-mapping")
	ResponseEntity<Void> updatePartnerRepMappings(
			@RequestBody final UpdatePartnerRepMappingsDTO input);

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/user-role-mapping")
	ResponseEntity<Void> updateUserRoleMappings(@RequestBody final UpdateUserRoleMappingInput input);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/partner-reps")
	ResponseEntity<ATSPartnerRepMappingsDTO> getPartnerRepMappings(@RequestParam("partnerId") final String partnerId);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/interview-structure")
	ResponseEntity<ATSInterviewStructureMappingsDTO> getATSInterviewStructureMappings(
			@RequestParam("partnerId") final String partnerId);

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/partner/{partnerId}/ats-secrets")
	List<ATSSecretDTO> getATSSecrets(@PathVariable("partnerId") String partnerId);
}
