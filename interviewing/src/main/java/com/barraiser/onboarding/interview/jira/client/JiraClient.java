/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.client;

import com.barraiser.onboarding.config.JiraConfig;
import com.barraiser.onboarding.interview.jira.dto.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "jira-cloud-client", url = JiraClient.BARRAISER_ATLASSIAN_BASE_URL, configuration = JiraConfig.class)
public interface JiraClient {

	String BARRAISER_ATLASSIAN_BASE_URL = "https://barraiser.atlassian.net";

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	ResponseEntity<EvaluationServiceDeskIssue> getEvaluationServiceDeskIssue(
			@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	InterviewServiceDeskIssue getInterviewServiceDeskIssue(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	ExpertIssue getExpertIssue(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	CompanyIssue getCompany(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	InterviewServiceDeskIssue getInterview(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	SkillIssue getSkill(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	InterviewStructureIssue getInterviewStructure(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	PartnerCompanyIssue getPartnerCompany(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	JobRoleIssue getJobRole(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	InterviewRoundIssue getInterviewRound(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	SkillWeightageIssue getSkillWeightage(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}")
	ResponseEntity<GenericIssue> getGenericIssue(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/3/issue/{issueIdOrKey}")
	GenericIssue getGenericIssueV3(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}/transitions")
	TransitionsResponse getTransitions(@PathVariable("issueIdOrKey") String issueIdOrKey);

	@GetMapping("/rest/api/2/issue/{issueIdOrKey}/comment/{id}")
	JiraCommentDTO getJiraIssueComment(@PathVariable("issueIdOrKey") String issueIdOrKey,
			@PathVariable("id") Long commentId);

	@PostMapping("/rest/api/2/issue/{issueIdOrKey}/comment")
	@Headers(value = "Content-Type: application/json")
	void addComment(@PathVariable("issueIdOrKey") String issueIdOrKey, @RequestBody JiraCommentDTO commentBody);

	@PostMapping("/rest/api/2/issue/{issueIdOrKey}/transitions")
	@Headers(value = "Content-Type: application/json")
	void updateIssueStatus(@PathVariable("issueIdOrKey") String issueIdOrKey, @RequestBody ObjectNode updateBody);

	/**
	 * Use this to set jira fields. Fields cannot be set to null by using this.
	 */
	@PutMapping("/rest/api/2/issue/{issueIdOrKey}")
	@Headers(value = "Content-Type: application/json")
	void setIssueFields(@PathVariable("issueIdOrKey") String issueIdOrKey,
			@RequestBody InterviewServiceDeskIssue updateBody);

	@PutMapping("/rest/api/2/issue/{issueIdOrKey}")
	@Headers(value = "Content-Type: application/json")
	void setIssueFields(@PathVariable("issueIdOrKey") String issueIdOrKey,
			@RequestBody EvaluationServiceDeskIssue updateBody);

	/**
	 * Use this to set/unset jira fields. Fields can be set to null by using this.
	 */
	@PutMapping("/rest/api/2/issue/{issueIdOrKey}")
	@Headers(value = "Content-Type: application/json")
	void updateIssueFields(@PathVariable("issueIdOrKey") String issueIdOrKey, @RequestBody ObjectNode updateBody);

	/**
	 * Use this to set/unset jira fields. Fields can be set to null by using this.
	 */
	@PostMapping("/rest/api/2/issue")
	@Headers(value = "Content-Type: application/json")
	CreateIssueResponse createEvaluationIssue(@RequestBody EvaluationServiceDeskIssue createBody);

	/**
	 * Use this to set/unset jira fields. Fields can be set to null by using this.
	 */
	@PostMapping("/rest/api/2/issue")
	@Headers(value = "Content-Type: application/json")
	<T> CreateIssueResponse createIssue(@RequestBody T createBody);

	@GetMapping("/rest/servicedeskapi/organization")
	ObjectNode getOrganizations();

	@GetMapping("/rest/api/3/issue/{issueIdOrKey}/changelog?startAt={start}")
	@Headers(value = "Content-Type: application/json")
	JiraChangeLogsResponse getChangeLogs(@PathVariable("issueIdOrKey") String issueIdOrKey,
			@PathVariable("start") Integer start);
}
