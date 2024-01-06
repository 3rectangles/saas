/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.sync;

import com.barraiser.ats_integrations.dto.ATSJobRoleMappingsDTO;
import com.barraiser.ats_integrations.dto.ATSPartnerRepMappingsDTO;
import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.commons.dto.jobRoleManagement.ATSPartnerRepInfo;
import com.barraiser.commons.dto.jobRoleManagement.InterviewStructureInput;
import com.barraiser.commons.dto.jobRoleManagement.JobRoleInput;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.errorhandling.exception.IllegalOperationException;
import com.barraiser.onboarding.interview.DeprecateJobRoleMutation;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.jobrole.InterviewStructureInfoUpdator;
import com.barraiser.onboarding.interview.jobrole.JobRoleManagementHelper;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.dal.LocationDAO;
import com.barraiser.onboarding.interview.jobrole.dal.TeamDAO;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.HiringManagerInfoSaver;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.RecruitersInfoSaver;
import com.barraiser.onboarding.user.CompanyManager;
import com.barraiser.onboarding.user.SkillManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.ATS_FULL_SYNC_STATUS_COMPLETED;
import static com.barraiser.onboarding.common.Constants.*;

// TODO : Not thought here about dirty writes | Have to explore locking.

//TODO: Class needs cleanup and refactoring. 

@Log4j2
@RequiredArgsConstructor
@Component
public class ATSJobRoleInfoSaver {

	private final JobRoleManagementHelper jobRoleManagementHelper;
	private final JobRoleManager jobRoleManager;
	private final InterviewStructureManager interviewStructureManager;
	private final DeprecateJobRoleMutation deprecateJobRoleMutation;
	private final InterviewStructureInfoUpdator interviewStructureInfoUpdator;
	private final HiringManagerInfoSaver atsHiringManagerInfoSaver;
	private final RecruitersInfoSaver atsRecruitersInfoSaver;
	private final ATSServiceClient atsServiceClient;
	private final ObjectMapper objectMapper;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final SkillManager skillManager;
	private final CompanyManager companyManager;

	private final SkillWeightageRepository skillWeightageRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;

	private final static String INTERVIEW_STRUCTURE_FLOW_NEW_VERSION = "1";

	@Transactional(rollbackOn = Exception.class)
	public Boolean save(final JobRoleInput input, final ATSProvider atsProvider, final String creationSource,
			final String creationSourceMeta)
			throws IOException, IllegalOperationException {

		Boolean isJobRoleVersionToBeUpdated = Boolean.TRUE;
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(input.getPartnerId()).get();

		/**
		 * STEP 1 : Get BR job role id and version,
		 * depending upon if its a creation case of updation.
		 *
		 * If job role exists , we will get the information of the latest job role
		 * version mapped to the ATS id.
		 * If job role does not exist , we will return version as -1 ie this is a case
		 * of creation.
		 */
		final Pair<String, Integer> previousVersionBrJobRoleIdVersionPair = this.getExistingJobRoleIdAndVersion(input);
		final String previousJRVersionBrJobRoleId = previousVersionBrJobRoleIdVersionPair.getKey();
		final Integer previousJRVersionBRVersion = previousVersionBrJobRoleIdVersionPair.getValue();

		log.info("Saving job role role info from ATS for job role ats id : %s", input.getAtsId());

		/**
		 * NOTE : If there are no interview structures in the job role means there is no
		 * critical information
		 * that needs version. Hence avoid updating the job role version
		 *
		 */
		if (input.getInterviewStructures() == null || input.getInterviewStructures().isEmpty()) {
			isJobRoleVersionToBeUpdated = Boolean.FALSE;
		}

		/**
		 * STEP 2 : Save job role level info
		 */
		final JobRoleDAO updatedJobRoleDAO = this.saveBasicJobRoleIfo(input, previousJRVersionBrJobRoleId,
				previousJRVersionBRVersion,
				creationSource,
				creationSourceMeta, isJobRoleVersionToBeUpdated);

		/**
		 * STEP 3 : Saving Interview structure Info
		 */
		List<InterviewStructureInput> interviewStructureInputsWithNewIds = new ArrayList<>();
		final Integer jobRoleUpdatedVersion = previousJRVersionBRVersion == -1 ? previousJRVersionBRVersion + 1
				: isJobRoleVersionToBeUpdated ? previousJRVersionBRVersion + 1
						: previousJRVersionBRVersion;

		/**
		 * Generating new interview structure ids for the interview structures to be
		 * added
		 * NOTE : We have assumed that a basic info sync from will not run after a full
		 * sync . Even if it does ,
		 * user will again press full sync for a job role they want to sync , so they
		 * can do that and will have to structure
		 * their interviews again.
		 */

		if (input.getInterviewStructures() != null && input.getInterviewStructures().size() != 0) {
			interviewStructureInputsWithNewIds = input.getInterviewStructures().stream().map(
					interviewStructureInput -> interviewStructureInput.toBuilder()
							.id(UUID.randomUUID().toString())
							.build())
					.collect(Collectors.toList());

			/**
			 * Adding default interview structure
			 */
			if (!Boolean.TRUE.equals(partnerCompanyDAO.getUseATSFeedback())) {
				final String defaultInterviewStructureId = this.addDefaultInterviewStructureToInput(input,
						interviewStructureInputsWithNewIds);
				this.copyRelevantSkillWeightagesOfJROfDefaultStructure(defaultInterviewStructureId,
						previousJRVersionBrJobRoleId, jobRoleUpdatedVersion);
			}

			// TODO: Currently we are not saving the is_default flag of interview structure
			// whenever each IS is getting copied.
			// Have to add fields in commons for InterviewStructureInput and do the needful.
			this.interviewStructureInfoUpdator.saveInterviewStructureInfo(previousJRVersionBrJobRoleId,
					previousJRVersionBRVersion,
					input.getPartnerId(),
					interviewStructureInputsWithNewIds,
					creationSource, INTERVIEW_STRUCTURE_FLOW_NEW_VERSION);

		} else {
			/**
			 * Adding default interview structure on job role creation first time
			 */
			if (previousJRVersionBRVersion == -1) {
				if (!Boolean.TRUE.equals(partnerCompanyDAO.getUseATSFeedback())) {
					final String defaultInterviewStructureId = this.addDefaultInterviewStructureToInput(input,
							interviewStructureInputsWithNewIds);
					this.copyRelevantSkillWeightagesOfJROfDefaultStructure(defaultInterviewStructureId,
							previousJRVersionBrJobRoleId, jobRoleUpdatedVersion);

					this.interviewStructureInfoUpdator.saveInterviewStructureInfo(previousJRVersionBrJobRoleId,
							previousJRVersionBRVersion,
							input.getPartnerId(),
							interviewStructureInputsWithNewIds,
							creationSource, INTERVIEW_STRUCTURE_FLOW_NEW_VERSION);
				}
			}
		}

		/**
		 * STEP 4: Copying skill weightages from old version of job role to new version.
		 *
		 * TODO: This can be problematic if those skills themselves dont exist in the
		 * new structure the user edits. But keeping it simple for now.
		 *
		 */
		if (previousJRVersionBRVersion != -1
				&& (input.getInterviewStructures() != null && input.getInterviewStructures().size() != 0)) { // Job
			this.copySkillWeightagesOfPreviousJRVersion(previousJRVersionBrJobRoleId, previousJRVersionBRVersion);
		}

		/**
		 * STEP 5 : Deprecating previous version of the job role
		 */
		if (isJobRoleVersionToBeUpdated && previousJRVersionBRVersion != -1) {
			this.deprecateJobRoleMutation.deprecateJobRole(previousJRVersionBrJobRoleId, previousJRVersionBRVersion);
		}

		/**
		 * STEP 6 : Saving job role and interview structure ats mappings
		 */

		final JobRoleInput updateJobRoleInfo = input.toBuilder().id(previousJRVersionBrJobRoleId)
				.version(previousJRVersionBRVersion == -1 ? previousJRVersionBRVersion + 1
						: isJobRoleVersionToBeUpdated ? previousJRVersionBRVersion + 1
								: previousJRVersionBRVersion) // Updating
				// Job
				// role
				// version
				.interviewStructures(interviewStructureInputsWithNewIds).build();

		this.jobRoleManagementHelper.updateAtsMappings(updateJobRoleInfo, atsProvider,
				updateJobRoleInfo.getPartnerId());

		return Boolean.TRUE;
	}

	private String addDefaultInterviewStructureToInput(final JobRoleInput input,
			List<InterviewStructureInput> interviewStructureInputsWithNewIds) {

		final InterviewStructureDAO defaultInterviewStructure = this.interviewStructureManager
				.getDefaultInterviewStructure(input.getPartnerId());

		final List<SkillDAO> skills = this.skillManager.getAllCategoriesCovered(defaultInterviewStructure.getId());

		if (defaultInterviewStructure != null) {
			interviewStructureInputsWithNewIds.add(

					this.objectMapper.convertValue(defaultInterviewStructure, InterviewStructureInput.class)
							.toBuilder()
							.id(UUID.randomUUID().toString())
							.domainId(defaultInterviewStructure.getDomainId())
							.isBrRound(defaultInterviewStructure.getIsBrRound())
							.duration(defaultInterviewStructure.getDuration())
							.categoryIds(skills.stream().map(x -> x.getId()).collect(Collectors.toList()))
							.build()// TODO : Verify if everything is getting copied
			);
		}

		return defaultInterviewStructure != null ? defaultInterviewStructure.getId() : null;
	}

	private void copyRelevantSkillWeightagesOfJROfDefaultStructure(final String defaultInterviewStructureId,
			final String targetJobRoleId, final Integer targetJobRoleVersion) {

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByInterviewStructureId(defaultInterviewStructureId);

		// Skills in the defaultInterview Structure + soft skills;
		final List<String> relevantSkills = this.skillManager.getAllCategoriesCovered(defaultInterviewStructureId)
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());

		// Explictly adding softskill
		relevantSkills.add(SOFT_SKILL_ID);

		this.copySkillWeightagesForJobRoles(jobRoleToInterviewStructureDAO.getJobRoleId(),
				jobRoleToInterviewStructureDAO.getJobRoleVersion(), targetJobRoleId, targetJobRoleVersion,
				relevantSkills);
	}

	private void copySkillWeightagesForJobRoles(final String sourceJobRoleId, final Integer sourceJobRoleVersion,
			final String destinationJobRoleId, final Integer destinationJobRoleVersion,
			final List<String> relevantSkills) {

		this.skillWeightageRepository.saveAll(
				this.skillWeightageRepository.findAllByJobRoleIdAndJobRoleVersion(sourceJobRoleId, sourceJobRoleVersion)
						.stream()
						.filter(x -> relevantSkills.contains(x.getSkillId()))
						.map(x -> x.toBuilder()
								.id(UUID.randomUUID().toString())
								.jobRoleId(destinationJobRoleId)
								.jobRoleVersion(destinationJobRoleVersion)
								.build())
						.collect(Collectors.toList()));
	}

	private void copySkillWeightagesOfPreviousJRVersion(final String oldJobRoleId, final Integer oldJobRoleVersion) {

		final List<SkillWeightageDAO> skillWeightagesUpdatedWithNewJobRoleInfo = this.skillWeightageRepository
				.findAllByJobRoleIdAndJobRoleVersion(oldJobRoleId, oldJobRoleVersion)
				.stream()
				.map(sw -> sw.toBuilder()
						.id(UUID.randomUUID().toString())
						.jobRoleId(oldJobRoleId)
						.jobRoleVersion(oldJobRoleVersion + 1)
						.build())
				.collect(Collectors.toList());

		this.skillWeightageRepository.saveAll(skillWeightagesUpdatedWithNewJobRoleInfo);
	}

	/**
	 * @param input
	 * @param previousJRVersionBRJobRoleId
	 *            new UUID for a job role getting created.
	 * @param previousJRVersionBRJobRoleVersion
	 *            -1 for a job role that is getting created for first time
	 * @param creationSource
	 * @param creationSourceMeta
	 * @return
	 * @throws IOException
	 * @throws IllegalOperationException
	 */
	public JobRoleDAO saveBasicJobRoleIfo(final JobRoleInput input, final String previousJRVersionBRJobRoleId,
			final Integer previousJRVersionBRJobRoleVersion,
			final String creationSource,
			final String creationSourceMeta,
			final Boolean isJobRoleVersionToBeUpdated)
			throws IOException, IllegalOperationException {

		final JobRoleDAO jobRoleInfoBeforeUpdate = previousJRVersionBRJobRoleVersion == -1
				? JobRoleDAO.builder().build()
				: this.jobRoleManager.getJobRole(previousJRVersionBRJobRoleId, previousJRVersionBRJobRoleVersion).get();

		Map<String, String> atsToBRPartnerRepIdMapping = this.atsServiceClient
				.getPartnerRepMappings(input.getPartnerId())
				.getBody().getPartnerRepMappings()
				.stream()
				.collect(Collectors.toMap(ATSPartnerRepMappingsDTO.PartnerRepMapping::getAtsPartnerRepId,
						ATSPartnerRepMappingsDTO.PartnerRepMapping::getBrPartnerRepId));

		final List<String> hiringManagers = input.getHiringManagers() == null ? null
				: this.atsHiringManagerInfoSaver.manageHiringManagersInfo(previousJRVersionBRJobRoleId,
						this.getBrUserIdsForATSUserIds(atsToBRPartnerRepIdMapping, input.getHiringManagers()));

		final List<String> recruiters = input.getRecruiters() == null ? null
				: this.atsRecruitersInfoSaver.manageRecruitersInfo(previousJRVersionBRJobRoleId,
						this.getBrUserIdsForATSUserIds(atsToBRPartnerRepIdMapping, input.getRecruiters()));

		final List<String> teams = input.getTeams() == null ? null
				: this.jobRoleManagementHelper
						.createOrUpdateATSTeams(input.getTeams(), input.getPartnerId(), creationSource,
								creationSourceMeta)
						.stream()
						.map(TeamDAO::getId)
						.collect(Collectors.toList());

		final List<String> locations = input.getLocations() == null ? null
				: this.jobRoleManagementHelper
						.createOrUpdateATSLocations(input.getLocations(), input.getPartnerId(), creationSource,
								creationSourceMeta)
						.stream()
						.map(LocationDAO::getId)
						.collect(Collectors.toList());

		String extSyncStatus = "";

		if (input.getBrStatusId() == JOBROLE_INTELLIGENCE_ENABLE_INTERVIEWS_NOT_STRUCTURED_STATUS_ID ||
				input.getBrStatusId() == JOBROLE_INTERVIEWS_STRUCTURED_STATUS_ID) {
			extSyncStatus = ATS_FULL_SYNC_STATUS_COMPLETED;
		}

		JobRoleDAO updatedJobRoleDAO = jobRoleInfoBeforeUpdate;
		final int version = previousJRVersionBRJobRoleVersion == -1 ? 0
				: isJobRoleVersionToBeUpdated ? previousJRVersionBRJobRoleVersion + 1
						: previousJRVersionBRJobRoleVersion;

		if (jobRoleInfoBeforeUpdate.getExtFullSync() != null && jobRoleInfoBeforeUpdate.getExtFullSync() == true
				&& input.getBrStatusId().equals(JOBROLE_INTELLIGENCE_DISABLED_STATUS_ID)) {
			// Updating only fields which are updated during mini sync when job role is
			// fully synced.
			updatedJobRoleDAO = updatedJobRoleDAO.toBuilder()
					.entityId(new VersionedEntityId(previousJRVersionBRJobRoleId,
							version))
					.internalDisplayName(input.getInternalDisplayName())
					.candidateDisplayName(input.getCandidateDisplayName())
					.activeCandidatesCountAggregate(previousJRVersionBRJobRoleVersion == -1 ? 0
							: this.jobRoleManager.getJobRoleActiveCandidateCountAggregate(previousJRVersionBRJobRoleId,
									previousJRVersionBRJobRoleVersion))
					.creationSource(creationSource)
					.creationMeta(creationSourceMeta)
					.teams(teams)
					.locations(locations)
					.extFullSyncStatus(extSyncStatus == "" ? jobRoleInfoBeforeUpdate.getExtFullSyncStatus()
							: extSyncStatus)
					.build();
		} else {
			updatedJobRoleDAO = updatedJobRoleDAO.toBuilder()
					.entityId(new VersionedEntityId(previousJRVersionBRJobRoleId,
							version))
					.partnerId(input.getPartnerId())
					.companyId(this.companyManager.getCompanyForPartner(input.getPartnerId()).getId())
					.internalDisplayName(input.getInternalDisplayName())
					.candidateDisplayName(input.getCandidateDisplayName())
					.brStatus(List.of(input.getBrStatusId()))
					.atsStatus(input.getAtsStatus())
					.hiringManagers(hiringManagers)
					.recruiters(recruiters)
					.locations(locations)
					.teams(teams)
					.activeCandidatesCountAggregate(previousJRVersionBRJobRoleVersion == -1 ? 0
							: this.jobRoleManager.getJobRoleActiveCandidateCountAggregate(previousJRVersionBRJobRoleId,
									previousJRVersionBRJobRoleVersion))
					.creationSource(creationSource)
					.creationMeta(creationSourceMeta)
					.extFullSyncStatus(extSyncStatus == "" ? jobRoleInfoBeforeUpdate.getExtFullSyncStatus()
							: extSyncStatus)
					.build();
		}

		this.jobRoleManager.saveJobRole(updatedJobRoleDAO);
		return updatedJobRoleDAO;
	}

	Set<String> getBrUserIdsForATSUserIds(final Map<String, String> atsToBrPartnerRepMapping,
			final List<ATSPartnerRepInfo> partnerRepInputList) {
		final Set<String> userIds = partnerRepInputList.stream()
				.map(x -> atsToBrPartnerRepMapping.get(x.getAtsPartnerRepId()))
				.filter(y -> y != null) // NOTE : Removing all users that were not successfully created and hence have
				// no mapping
				.collect(Collectors.toSet());

		return userIds;
	}

	/**
	 * * We first check if ats mapping is present , so we don't end up creating the
	 * same job role
	 * again and again and polluting the db.
	 * - if ATS mapping exists we will get the barraiser job role id from that and
	 * create a new version of it and save it.
	 * - if ATS mapping does not exist we will create a fresh job role
	 */
	private Pair<String, Integer> getExistingJobRoleIdAndVersion(final JobRoleInput input) throws IOException {
		final String jobRoleId;
		final Integer jobRoleVersion;

		Map<String, String> atsToBRJobRoleMapping = this.atsServiceClient.getATSJobRoleMappings(input.getPartnerId())
				.getBody().getJobRoleMappings().stream()
				.collect(Collectors.toMap(ATSJobRoleMappingsDTO.JobRoleMapping::getBrJobRoleId,
						ATSJobRoleMappingsDTO.JobRoleMapping::getAtsJobRoleId));

		/**
		 * If ats to br job role mapping is absent , create fresh job role in BR system.
		 */
		if (!atsToBRJobRoleMapping.containsKey(input.getAtsId())) {
			jobRoleId = UUID.randomUUID().toString();
			jobRoleVersion = -1;
		} else {
			jobRoleId = atsToBRJobRoleMapping.get(input.getAtsId());
			final Integer currentLatestJobRoleVersion = this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get()
					.getEntityId().getVersion();
			jobRoleVersion = currentLatestJobRoleVersion;
		}

		return new Pair<>(jobRoleId, jobRoleVersion);
	}

}
