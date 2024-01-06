/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "job-role-interview-structure-mapping-feign-client", url = "http://localhost:5000")
public interface JobRoleInterviewStructureMappingFeignClient {

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/interception/jobRole/interviewStructure")
	@Headers(value = "Content-Type: application/json")
	String getBRInterviewStructureId(
			@RequestParam("partnerId") final String partnerId,
			@RequestParam("jobRoleId") final String jobRoleId,
			@RequestParam("interviewStructureIds") final List<String> interviewStructureIds);

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partner_id}/interviewStructure")
	String getInterviewStructureId(@PathVariable("partner_id") String partnerId);
}
