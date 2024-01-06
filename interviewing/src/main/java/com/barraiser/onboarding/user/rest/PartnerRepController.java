/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.rest;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.common.graphql.types.PartnerRepDetails;
import com.barraiser.commons.dto.ats.ATSSecretDTO;
import com.barraiser.commons.dto.common.EntityOperationError;
import com.barraiser.commons.dto.jobRoleManagement.ATSPartnerRepInfo;
import com.barraiser.commons.dto.jobRoleManagement.BulkAddATSPartnerRepsRequest;
import com.barraiser.commons.dto.jobRoleManagement.BulkAddATSPartnerRepsResponse;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.ats_integrations.sync.ATSPartnerRepInfoManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.user.InterviewerAdditionService;
import com.barraiser.onboarding.user.PartnerRepAdditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Headers;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.barraiser.common.constants.Constants.CREATION_SOURCE_ATS_INTEGRATION;
import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@Log4j2
@RestController
@AllArgsConstructor
public class PartnerRepController {

	private PartnerRepAdditionService partnerRepAdditionService;
	private ATSPartnerRepInfoManager atsPartnerRepInfoManager;
	private PartnerRepsMapper partnerRepsMapper;
	private UserDetailsRepository userDetailsRepository;
	private PartnerRepsRepository partnerRepsRepository;
	private ATSServiceClient atsServiceClient;
	private ObjectMapper objectMapper;
	private InterviewerAdditionService interviewerAdditionService;

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partnerRep")
	public ResponseEntity<String> addPartnerRep(
			@RequestBody final PartnerAccessInput addPartnerRepRequest) throws Exception {
		return ResponseEntity.ok(this.partnerRepAdditionService.addPartnerRep(addPartnerRepRequest));
	}

	@PutMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/addInterviewer")
	@Headers(value = "Content-Type: application/json")
	ResponseEntity<String> addInterviewer(@RequestBody PartnerAccessInput partnerAccessInput) {

		return ResponseEntity.ok(this.interviewerAdditionService.addInterviewer(partnerAccessInput));
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/ats-partner-reps/bulk")
	public ResponseEntity<BulkAddATSPartnerRepsResponse> addPartnerRepsFromATS(
			@RequestBody final BulkAddATSPartnerRepsRequest bulkAddATSPartnerRepsRequest) throws Exception {
		final List<EntityOperationError> partnerRepAdditionErrors = new ArrayList<>();

		// NOTE : We are safely assuming that there is only one integration per partner
		// for now.
		final ATSSecretDTO atsSecretDTO = this.atsServiceClient
				.getATSSecrets(bulkAddATSPartnerRepsRequest.getPartnerId()).get(0);

		for (ATSPartnerRepInfo partnerRepInfo : bulkAddATSPartnerRepsRequest.getPartnerReps()) {

			try {
				this.atsPartnerRepInfoManager.process(bulkAddATSPartnerRepsRequest.getPartnerId(),
						atsSecretDTO.getAtsProvider(), partnerRepInfo,
						CREATION_SOURCE_ATS_INTEGRATION,
						bulkAddATSPartnerRepsRequest.getSourceMeta());
			} catch (Exception e) {

				log.error("An error occurred adding ats partner rep with ats id : {} for partner {} and info {} : ",
						partnerRepInfo.getAtsPartnerRepId(), bulkAddATSPartnerRepsRequest.getPartnerId(),
						this.objectMapper.writeValueAsString(partnerRepInfo), e, e);
				partnerRepAdditionErrors.add(
						EntityOperationError.builder()
								.entityIdentifier(partnerRepInfo.getAtsPartnerRepId())
								.errorCode("1")
								.errorMessage("Error adding partner rep.")
								.build());
			}
		}

		return ResponseEntity.ok(
				BulkAddATSPartnerRepsResponse.builder()
						.partnerRepAdditionErrors(partnerRepAdditionErrors)
						.build());
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/getPartnerRepDetails/{partnerRepEmailId}/userEmail")
	ResponseEntity<PartnerRepDetails> getPartnerRepDetails(
			@PathVariable("partnerRepEmailId") final String partnerRepEmailId) {
		Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository.findByEmail(partnerRepEmailId);

		Optional<PartnerRepsDAO> partnerRepsDAOOptional = this.partnerRepsRepository
				.findByPartnerRepId(userDetailsDAO.get().getId());

		return ResponseEntity.ok(
				this.partnerRepsMapper.toPartnerRepDetails(partnerRepsDAOOptional.get()));
	}

}
