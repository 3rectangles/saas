/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.endpoint;

import com.barraiser.ats_integrations.calendar_interception.ATSCommunicationHandler;
import com.barraiser.ats_integrations.common.JobRoleConfigService;
import com.barraiser.ats_integrations.common.dto.ATSEvaluationDetailsDTO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.dto.*;
import com.barraiser.ats_integrations.services.PartnerRepConfigService;
import com.barraiser.ats_integrations.services.UserConfigService;
import com.barraiser.commons.dto.User.role.UpdateUserRoleMappingInput;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@AllArgsConstructor
public class ATSController {
	final static String SERVICE_CONTEXT_PATH = "/ats";

	private final JobRoleConfigService jobRoleConfigService;
	private final PartnerRepConfigService partnerRepConfigService;
	private final UserConfigService userConfigService;
	private final ATSCommunicationHandler atsCommunicationHandler;
	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/ats-job-role")
	ResponseEntity<ATSJobRoleDTO> getAtsJobRoleIdFromBrJobRoleId(
			@RequestParam("br-job-role-id") final String brJobRoleId) {
		final String atsJobRoleId = this.jobRoleConfigService.getAtsJobRoleId(brJobRoleId);
		if (atsJobRoleId == null) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.ok().body(ATSJobRoleDTO.builder().id(atsJobRoleId).build());
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/job-role")
	ResponseEntity<ATSJobRoleMappingsDTO> getAllATSJobRoleMappings(@RequestParam("partnerId") final String partnerId) {
		return ResponseEntity.ok().body(ATSJobRoleMappingsDTO.builder()
				.jobRoleMappings(this.jobRoleConfigService.getATSJobRoleMappings(partnerId))
				.build());
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/interview-structure")
	ResponseEntity<ATSInterviewStructureMappingsDTO> getATSInterviewStructureMappings(
			@RequestParam("partnerId") final String partnerId) {
		return ResponseEntity.ok().body(ATSInterviewStructureMappingsDTO.builder()
				.interviewStructureMappings(this.jobRoleConfigService.getATSInterviewStructureMappings(partnerId))
				.build());
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/ats-interview-structure")
	ResponseEntity<ATSInterviewStructureDTO> getAtsInterviewStructureId(
			@RequestParam("br-interview-structure-id") final String brInterviewStructureId) {
		final String atsInterviewStructureId = this.jobRoleConfigService
				.getAtsInterviewStructureId(brInterviewStructureId);
		if (atsInterviewStructureId == null) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.ok().body(ATSInterviewStructureDTO.builder().id(atsInterviewStructureId).build());
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/job-role-mapping")
	ResponseEntity<Void> updateJobRoleMappings(
			@RequestBody final UpdateAtsJobRoleMappingDTO request) {
		this.jobRoleConfigService.updateJobRoleMappings(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/user-role-mapping")
	ResponseEntity<Void> updateUserRoleMappings(
			@RequestBody final UpdateUserRoleMappingInput input) {
		this.userConfigService.updateUserRoleMapping(input);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/user-role")
	ResponseEntity<ATSUserRoleMappingsDTO> getATSUserRoleMappings(@RequestParam("partnerId") final String partnerId) {
		return ResponseEntity.ok().body(
				ATSUserRoleMappingsDTO.builder()
						.userRoleMappings(this.userConfigService.getUserRoleMappings(partnerId))
						.build());
	}

	@PostMapping(value = SERVICE_CONTEXT_PATH + "/partner-rep-mapping")
	ResponseEntity<Void> updatePartnerRepMappings(
			@RequestBody final UpdatePartnerRepMappingsDTO input) {
		this.partnerRepConfigService.updatePartnerRepMappings(input);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = SERVICE_CONTEXT_PATH + "/mappings/partner-reps")
	ResponseEntity<ATSPartnerRepMappingsDTO> getPartnerRepMappings(@RequestParam("partnerId") final String partnerId) {
		return ResponseEntity.ok().body(
				ATSPartnerRepMappingsDTO.builder()
						.partnerRepMappings(this.partnerRepConfigService.getPartnerRepMappings(partnerId))
						.build());
	}

	@SneakyThrows
	@PostMapping(value = SERVICE_CONTEXT_PATH + "/post-note")
	ResponseEntity<Void> postNote(
			@RequestBody final PostATSNoteDTO input) {

		final ATSToBREvaluationDAO atsToBREvaluationDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationId(input.getEvaluationId()).get();

		final PartnerATSIntegrationDAO partnerATSIntegrationDAO = this.partnerATSIntegrationRepository
				.findAllByPartnerId(input.getPartnerId()).get(0);

		this.atsCommunicationHandler.postNoteOnApplication(input.getMessage(),
				partnerATSIntegrationDAO.getAtsAggregator(),
				ATSProvider.fromString(partnerATSIntegrationDAO.getAtsProvider()),
				ATSEvaluationDetailsDTO.builder()
						.ATSEvaluationId(atsToBREvaluationDAO.getAtsEvaluationId())
						.ATSRemoteData(atsToBREvaluationDAO.getRemoteData())
						.partnerId(input.getPartnerId())
						.build());

		return ResponseEntity.ok().build();
	}

}
