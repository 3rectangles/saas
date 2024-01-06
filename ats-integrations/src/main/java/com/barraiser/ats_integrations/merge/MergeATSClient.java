/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.merge.DTO.*;
import com.barraiser.ats_integrations.merge.responses.ApplicationsResponse;
import com.barraiser.ats_integrations.merge.responses.CandidatesResponse;
import com.barraiser.ats_integrations.merge.responses.InterviewsResponse;
import com.barraiser.ats_integrations.merge.responses.JobsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "merge-ats-client", url = MergeATSClient.MERGE_ATS_BASE_URL)
public interface MergeATSClient {
	String MERGE_ATS_BASE_URL = "https://api.merge.dev/api/ats/v1";
	String AUTHORIZATION = "Authorization";
	String X_ACCOUNT_TOKEN = "X-Account-Token";

	@GetMapping(value = "/account-details", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<AccountDetailsDTO> getAccountDetails(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken);

	@GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobsResponse> getJobs(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken);

	@GetMapping(value = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobsResponse> getJobs(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@RequestParam("cursor") final String paginationCursor);

	@GetMapping(value = "/interviews?remote_id={remote_id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<InterviewsResponse> getRemoteInterview(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("remote_id") final String remoteId);

	@GetMapping(value = "/interviews?remote_id={remote_id}&include_remote_data=true", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<InterviewsResponse> getRemoteInterviewWithRemoteData(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("remote_id") final String remoteId);

	@GetMapping(value = "/interviews?application_id={applicationId}&job_interview_stage_id={interviewStructureId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<InterviewsResponse> getInterview(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("applicationId") final String applicationId,
			@PathVariable("interviewStructureId") final String interviewStructureId);

	@GetMapping(value = "/candidates?remote_id={remote_id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CandidatesResponse> getRemoteCandidate(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("remote_id") final String remoteId);

	@GetMapping(value = "/candidates/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CandidateDTO> getCandidate(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@GetMapping(value = "/applications?remote_id={remote_id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ApplicationsResponse> getRemoteApplication(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("remote_id") final String remoteId);

	@GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ApplicationDTO> getApplication(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@GetMapping(value = "/applications/{id}?include_remote_data=true", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ApplicationDTO> getApplicationWithRemoteData(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@GetMapping(value = "/applications?remote_id={remote_id}&include_remote_data=true", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ApplicationsResponse> getRemoteApplicationWithRemoteData(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("remote_id") final String remoteId);

	@GetMapping(value = "/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobDTO> getJob(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@GetMapping(value = "/job-interview-stages/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobInterviewStageDTO> getJobInterviewStage(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@GetMapping(value = "/attachments/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<AttachmentDTO> getAttachment(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@PathVariable("id") final String id);

	@PostMapping(value = "passthrough", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PassthroughResponseDTO> callPassthrough(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@RequestBody final PassthroughInputDTO passthroughInputDTO);

	@PostMapping(value = "passthrough", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PassthroughNoDataResponseDTO> callPassthroughNoData(
			@RequestHeader(AUTHORIZATION) final String authorization,
			@RequestHeader(X_ACCOUNT_TOKEN) final String xAccountToken,
			@RequestBody final PassthroughInputDTO passthroughInputDTO);

}
