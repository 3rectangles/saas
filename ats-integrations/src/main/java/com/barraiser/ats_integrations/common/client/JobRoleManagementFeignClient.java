/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.common.graphql.types.JobRole;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "job-role-management-feign-client", url = "http://localhost:5000")
public interface JobRoleManagementFeignClient {
	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/jobRole")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<JobRole> getJobRole(@RequestParam("interviewStructureId") final String interviewStructureId);

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/jobRole/isInterceptionEnabled")
	Boolean isJobRoleInterceptionEnabled(@RequestParam("jobRoleId") final String jobRoleId);
}
