/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.requests.*;
import com.barraiser.ats_integrations.lever.responses.*;
import feign.Param;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@FeignClient(name = "lever-client", url = "${lever.api-client}")
public interface LeverClient {
	@GetMapping(value = "/postings", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostingsResponse> getAllPostings(
			@RequestHeader("Authorization") final String authorization, @RequestParam("offset") final String offset);

	@GetMapping(value = "/postings/{posting}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PostingResponse> getPosting(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("posting") final String leverPostingId);

	@GetMapping(value = "/opportunities/{opportunity}/applications/{application}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ApplicationResponse> getApplication(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId,
			@PathVariable("application") final String applicationId);

	@GetMapping(value = "/opportunities/{opportunity}/applications", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<LeverApplicationsResponse> getApplications(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId);

	@GetMapping(value = "/opportunities/{opportunity}/interviews/{interview}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<LeverInterviewResponse> getInterview(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId,
			@PathVariable("interview") final String interviewId);

	@GetMapping(value = "/opportunities/{opportunity}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpportunityResponse> getOpportunity(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId);

	@GetMapping(value = "/opportunities/{opportunity}/resumes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResumesResponse> getResumes(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId);

	@GetMapping(value = "/users/{user}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<UserResponse> getUser(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("user") final String userId);

	@PostMapping(value = "/opportunities/{opportunity}/notes", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<NotesResponse> addNotesToOpportunity(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId,
			@RequestBody final NotesRequestBody notesRequestBody);

	@GetMapping
	Response downloadResumeFile(
			@Param("url") final URI url,
			@RequestHeader("Authorization") final String authorization);

	@GetMapping(value = "/stages/{stage}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<StageResponse> getStage(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("stage") final String stageId);

	@PostMapping(value = "/opportunities/{opportunity}/addTags", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<OpportunityResponse> addTagsToOpportunity(
			@RequestHeader("Authorization") final String authorization,
			@PathVariable("opportunity") final String opportunityId,
			@RequestBody final AddTagsRequestBody addTagsRequestBody);
}
