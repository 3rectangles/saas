/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.rest;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.ATSToBRInterviewStructureMappingRepository;
import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.commons.dto.ats.ATSSecretDTO;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.commons.dto.common.EntityOperationError;
import com.barraiser.commons.dto.jobRoleManagement.BulkAddJobRolesRequest;
import com.barraiser.commons.dto.jobRoleManagement.BulkAddJobRolesResponse;
import com.barraiser.commons.dto.jobRoleManagement.JobRoleInput;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.ats_integrations.publisher.SqsProducer;
import com.barraiser.onboarding.ats_integrations.sync.ATSJobRoleInfoSaver;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.ATS_FULL_SYNC_STATUS_PENDING;
import static com.barraiser.common.constants.Constants.CREATION_SOURCE_ATS_INTEGRATION;
import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@RestController
@Log4j2
@AllArgsConstructor
public class JobRoleManagementController {

	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final JobRoleRepository jobRoleRepository;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final JobRoleMapper jobRoleMapper;
	private final SqsProducer sqsProducer;
	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final ATSServiceClient atsServiceClient;
	private final JobRoleManager jobRoleManager;
	private final ATSToBRInterviewStructureMappingRepository atsToBRInterviewStructureMappingRepository;

	private static final String JOB_ROLE_ATS_SYNC_TYPE_FULL = "FULL"; // full job sync as per availability service where
	// we are syncing full information of the job
	// role
	private static final String JOB_ROLE_ATS_SYNC_TYPE_BASIC = "BASIC"; // basic job info sync as per availability
	// service
	// where we are syncing just the basic
	// information of the job role

	private static final String POST_BASIC_ATS_JOB_SYNC_STATUS_ID = "d53840f1-f03f-44df-a172-b27df1fdbe89"; // TODO:
	// Confirm
	// status
	private static final String POST_FULL_ATS_JOB_SYNC_STATUS_ID = "d58298c8-47cf-49cc-a2a4-7175f22dd62b";

	private ATSJobRoleInfoSaver atsJobRoleInfoSaver;

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/jobRole")
	public ResponseEntity<JobRole> getJobRole(@RequestParam("interviewStructureId") final String interviewStructureId) {

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByInterviewStructureId(interviewStructureId);
		final String jobRoleId = jobRoleToInterviewStructureDAO.getJobRoleId();

		final Optional<JobRoleDAO> jobRoleDAOOptional = this.jobRoleRepository
				.findTopByEntityIdIdOrderByEntityIdVersionDesc(jobRoleId);

		if (jobRoleDAOOptional.isEmpty()) {
			return ResponseEntity.ok(null);
		}

		return ResponseEntity.ok(this.jobRoleMapper.toJobRole(jobRoleDAOOptional.get()));
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/ats-job-roles/sync-type/{syncType}/bulk")
	public ResponseEntity<BulkAddJobRolesResponse> upsertJobRoles(@PathVariable("syncType") String syncType,
			@RequestBody BulkAddJobRolesRequest bulkAddJobRolesRequest) throws JsonProcessingException {

		// NOTE : We are safely assuming that there is only one integration per partner
		// for now.
		final ATSSecretDTO atsSecretDTO = this.atsServiceClient
				.getATSSecrets(bulkAddJobRolesRequest.getPartnerId()).get(0);

		if (JOB_ROLE_ATS_SYNC_TYPE_FULL.equals(syncType)) {
			return ResponseEntity.ok(this.saveJobRoleData(bulkAddJobRolesRequest, atsSecretDTO.getAtsProvider(),
					POST_FULL_ATS_JOB_SYNC_STATUS_ID));
		} else if (JOB_ROLE_ATS_SYNC_TYPE_BASIC.equals(syncType)) {
			return ResponseEntity.ok(this.saveJobRoleData(bulkAddJobRolesRequest, atsSecretDTO.getAtsProvider(),
					POST_BASIC_ATS_JOB_SYNC_STATUS_ID));
		} else {
			log.error("Invalid request. No sync type %s supported", syncType);
			return ResponseEntity.badRequest().build();
		}
	}

	private BulkAddJobRolesResponse saveJobRoleData(final BulkAddJobRolesRequest bulkAddJobRolesRequest,
			final ATSProvider atsProvider,
			final String brJobRoleStatusId) throws JsonProcessingException {

		final List<EntityOperationError> entityOperationErrorList = new ArrayList<>();
		log.info("TO BE REMOVED: Printing request : {}", this.objectMapper.writeValueAsString(bulkAddJobRolesRequest));

		for (final JobRoleInput jobRoleInput : bulkAddJobRolesRequest.getJobRoles()) {
			try {
				final JobRoleInput updatedJobRoleInput = jobRoleInput.toBuilder()
						.brStatusId(brJobRoleStatusId)
						.partnerId(bulkAddJobRolesRequest.getPartnerId())
						.build();

				this.atsJobRoleInfoSaver.save(updatedJobRoleInput, atsProvider,
						CREATION_SOURCE_ATS_INTEGRATION,
						bulkAddJobRolesRequest.getSourceMeta());

			} catch (Exception e) {
				log.error("An error occured adding ats job role with ats id : {} for partner {} : with input : {} ",
						jobRoleInput.getAtsId(), jobRoleInput.getPartnerId(),
						this.objectMapper.writeValueAsString(jobRoleInput), e, e);

				entityOperationErrorList.add(EntityOperationError.builder()
						.entityIdentifier(jobRoleInput.getAtsId())
						.errorCode("1")
						.errorMessage("Error adding job role.")
						.build());
			}
		}
		return BulkAddJobRolesResponse.builder()
				.jobRoleAdditionErrors(entityOperationErrorList)
				.build();
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/job/full-sync/status/{jobRoleId}")
	ResponseEntity<String> getJobSyncStatus(@PathVariable final String jobRoleId) throws Exception {
		return ResponseEntity.ok(this.jobRoleRepository.findTopByEntityIdIdOrderByEntityIdVersionDesc(jobRoleId)
				.get().getExtFullSyncStatus());
	}

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/job/full-sync/{jobRoleId}")
	ResponseEntity<Void> jobFullSync(@PathVariable final String jobRoleId) throws Exception {
		final JobRoleDAO jobRoleDAO = this.jobRoleRepository.findTopByEntityIdIdOrderByEntityIdVersionDesc(jobRoleId)
				.get();
		if (BooleanUtils.isNotTrue(jobRoleDAO.getExtFullSync())) {
			JobRoleDAO dao = jobRoleDAO.toBuilder().extFullSync(true)
					.extFullSyncStatus(ATS_FULL_SYNC_STATUS_PENDING) // TODO: Update on ats integration completion.
					.build();
			this.jobRoleRepository.save(dao);
			String queueAtsIntegration = this.environment.getProperty("queue.atsIntegration");
			if (StringUtils.isNotEmpty(queueAtsIntegration)) {
				final Optional<ATSJobPostingToBRJobRoleDAO> brJobRoleId = this.atsJobPostingToBRJobRoleRepository
						.findByBrJobRoleId(jobRoleId);
				if (brJobRoleId.isPresent()) {
					this.sqsProducer.publish(queueAtsIntegration,
							Map.of("partnerId", dao.getPartnerId(), "externalJobId",
									brJobRoleId.get().getAtsJobPostingId()));
				}

			}
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/job/full-sync/{partnerId}")
	ResponseEntity<Set<String>> jobIdByFullSync(@PathVariable final String partnerId) {
		final List<JobRoleDAO> jobRoleDAO = this.jobRoleRepository.findAllByPartnerIdAndExtFullSync(partnerId, true);
		Set<String> collect = jobRoleDAO.stream().map(JobRoleDAO::getEntityId).filter(Objects::nonNull)
				.map(VersionedEntityId::getId).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(collect)) {
			return ResponseEntity.ok()
					.body(this.atsJobPostingToBRJobRoleRepository.findAllByBrJobRoleIdIn(collect).stream()
							.map(ATSJobPostingToBRJobRoleDAO::getAtsJobPostingId).collect(Collectors.toSet()));
		}
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/jobRole/isInterceptionEnabled")
	Boolean isJobRoleInterceptionEnabled(@RequestParam("jobRoleId") final String jobRoleId) {
		JobRoleDAO jobRoleDAO = this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get();
		return jobRoleDAO.getExtFullSync() == null ? false : jobRoleDAO.getExtFullSync();
	}

}
