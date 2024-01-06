/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.smartRecruiters.DTO.*;
import com.barraiser.ats_integrations.smartRecruiters.DTO.TagsDTO;
import com.barraiser.ats_integrations.smartRecruiters.requests.MessageRequest;
import com.barraiser.ats_integrations.smartRecruiters.responses.MessageResponse;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "smart-recruiters-client", url = "https://api.smartrecruiters.com")
public interface SmartRecruitersClient {
	@GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobsDTO> getJobs(
			@RequestHeader("X-SmartToken") final String apikey,
			@RequestParam("limit") final Integer limit);

	@GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobsDTO> getJobsOfCurrentPage(
			@RequestHeader("X-SmartToken") final String apikey,
			@RequestParam("pageId") final String pageId,
			@RequestParam("limit") final Integer limit);

	@PostMapping(value = "/webhooks-api/v201907/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<WebhookSubscriptionResponseDTO> subscribeWebhook(
			@RequestHeader("X-SmartToken") final String apiKey,
			@RequestBody final WebhookSubscriptionRequestDTO requestDTO);

	@PutMapping(value = "/webhooks-api/v201907/subscriptions/{webhookSubscriptionId}/activation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	void activateWebhookSubscription(
			@RequestHeader("X-SmartToken") final String apiKey,
			@PathVariable("webhookSubscriptionId") final String webhookSubscriptionId);

	@GetMapping(value = "/candidates/{candidateId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CandidateDTO> getCandidate(
			@RequestHeader("X-SmartToken") final String apikey,
			@PathVariable("candidateId") final String candidateId);

	@GetMapping(value = "/candidates/{candidateId}/jobs/{jobId}/attachments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<AttachmentsDTO> getAttachments(
			@RequestHeader("X-SmartToken") final String apikey,
			@PathVariable("candidateId") final String candidateId,
			@PathVariable("jobId") final String jobId);

	@GetMapping(value = "/candidates/attachments/{attachmentId}")
	Response downloadAttachment(
			@RequestHeader("X-SmartToken") final String apikey,
			@PathVariable("attachmentId") final String attachmentId);

	@GetMapping(value = "/jobs/{jobId}/hiring-team", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<HiringTeamDTO> getHiringTeam(
			@RequestHeader("X-SmartToken") final String apikey,
			@PathVariable("jobId") final String jobId);

	@GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<UserDTO> getUser(
			@RequestHeader("X-SmartToken") final String apikey,
			@PathVariable("userId") final String userId);

	@PostMapping(value = "/candidates/{candidateId}/tags", consumes = MediaType.APPLICATION_JSON_VALUE)
	void addTags(
			@RequestHeader("X-SmartToken") final String apiKey,
			@PathVariable("candidateId") final String candidateId,
			@RequestBody final TagsDTO requestBody);

	@PostMapping(value = "/messages/shares", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<MessageResponse> shareMessage(
			@RequestHeader("X-SmartToken") final String apiKey,
			@RequestBody final MessageRequest requestBody);
}
