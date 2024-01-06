/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.data_science.DTO.InterviewSkillsAndValuesRequestDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "interview-structure-skills-values-feign-client", url = "${interview-structure-skills-values-url}")
public interface InterviewStructureSkillsValuesFeignClient {
	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> getInterviewStructureSkillsValues(
			@RequestBody final InterviewSkillsAndValuesRequestDTO interviewSkillsAndValuesRequestDTO);
}
