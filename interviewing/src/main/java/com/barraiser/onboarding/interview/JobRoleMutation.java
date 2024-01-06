/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.ats_integrations.dto.UpdateAtsJobRoleMappingDTO;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingUpdationResult;
import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.common.graphql.input.InterviewStructureInput;
import com.barraiser.common.graphql.input.JobRoleInput;
import com.barraiser.common.graphql.input.SkillInput;
import com.barraiser.common.graphql.types.*;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.ats_integrations.ATSServiceClient;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleStatusManager;
import com.barraiser.onboarding.interview.validators.JobRoleInputValidator;
import com.barraiser.onboarding.jobRoleManagement.JobRolePricingDetailsUpdator;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.HiringManagerInfoSaver;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.HiringTeamMemberInfoSaver;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.RecruitersInfoSaver;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class JobRoleMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final JobRoleManager jobRoleManager;
	private final JobRoleStatusManager jobRoleStatusManager;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final SkillWeightageRepository skillWeightageRepository;
	private final JobRoleInputValidator jobRoleInputValidator;
	private final JobRoleHistoryRepository jobRoleHistoryRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final DefaultQuestionsRepository defaultQuestionsRepository;
	private final DocumentRepository documentRepository;
	private final ExpertSkillsRepository expertSkillsRepository;
	private final JobRolePricingDetailsUpdator jobRolePricingDetailsUpdator;
	private final DeprecateJobRoleMutation deprecateJobRoleMutation;
	private final ATSServiceClient atsServiceClient;
	private final PartnerConfigManager partnerConfigManager;
	private final HiringManagerInfoSaver hiringManagerInfoSaver;
	private final RecruitersInfoSaver recruitersInfoSaver;
	private final HiringTeamMemberInfoSaver hiringTeamMemberInfoSaver;
	private final UserDetailsRepository userDetailsRepository;

	private final static Integer DEFAULT_JOB_ROLE_VERSION = 0;

	@Override
	public String name() {
		return "createJobRole";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		JobRoleInput input = this.graphQLUtil.getInput(environment, JobRoleInput.class);
		final ArrayList<JobRoleCreationError> errors = new ArrayList<>();
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		errors.addAll(this.checkInput(input));
		if (errors.size() > 0) {
			return JobRoleCreationResult.builder().success(Boolean.FALSE).errors(errors).type("error").build();
		}
		final List<String> errorsForNoAvailableExpertForSpecificSkills = this
				.checkIfExpertExistsForSpecificSkillsOrNot(input.getInterviewStructures());
		if (Boolean.TRUE.equals((input.getCheckSpecificSkillExpertAvailability() != null)
				&& input.getCheckSpecificSkillExpertAvailability())
				&& errorsForNoAvailableExpertForSpecificSkills != null) {
			return JobRoleCreationResult.builder()
					.expertsNotFoundForSpecificSkills(errorsForNoAvailableExpertForSpecificSkills).type("warning")
					.build();
		}
		if (input.getId() != null) {
			this.deprecateJobRoleMutation.deprecateJobRole(input.getId(), input.getVersion());
		}

		final String jobRoleId = input.getId() != null ? input.getId() : UUID.randomUUID().toString();
		final Integer jobRoleVersion = this.getVersionForJobRoleToBeCreated(input.getId());
		final JobRoleDAO jobRoleDAO = this.saveJobRoleInput(input, jobRoleId, jobRoleVersion);
		final List<InterviewStructureInput> interviewStructureInputs = input.getInterviewStructures().stream().map(
				interviewStructureInput -> interviewStructureInput.toBuilder().id(UUID.randomUUID().toString()).build())
				.collect(Collectors.toList());
		this.saveRoundLevelInterviewStructure(jobRoleId, jobRoleVersion, interviewStructureInputs);
		this.saveSkillWeightages(jobRoleId, jobRoleVersion, input);
		this.saveJobRoleHistory(input, jobRoleId, jobRoleVersion);
		final JobRoleCreationResult jobRoleCreationResult = this.updateJobRoleBasedPricing(jobRoleId,
				interviewStructureInputs, input.getCompanyId(), user.getUserName());
		if (jobRoleCreationResult != null) {
			return jobRoleCreationResult;
		}
		input = input.toBuilder().id(jobRoleId).version(jobRoleVersion)
				.interviewStructures(interviewStructureInputs).build();
		this.updateAtsMappings(input, input.getCompanyId());

		return JobRoleCreationResult.builder().jobRoleId(jobRoleId)
				.jobRoleVersion(jobRoleVersion)
				.success(Boolean.TRUE).errors(null).type("All good")
				.build();
	}

	private Integer getVersionForJobRoleToBeCreated(final String jobRoleId) {
		return jobRoleId == null
				? DEFAULT_JOB_ROLE_VERSION
				: this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId)
						.get()
						.getEntityId()
						.getVersion() + 1;
	}

	private ArrayList<JobRoleCreationError> checkInput(final JobRoleInput input) {
		final ArrayList<JobRoleCreationError> errors = new ArrayList<>();

		// Commenting these validations as they are conditional based on a config , and
		// already applied in FE. Will be applied in backend later.
		// errors.addAll(this.jobRoleInputValidator.validate(input));
		errors.addAll(this.jobRoleInputValidator.checkSize(input.getInterviewStructures().size()));
		errors.addAll(this.jobRoleInputValidator.checkSkillWeightageSumAndIndividualWeightage(
				input.getSkillWeightages(), input.getInterviewStructures()));
		return errors;
	}

	public JobRoleDAO saveJobRoleInput(final JobRoleInput input, final String jobRoleId, final Integer jobRoleVersion) {
		Optional<DocumentDAO> documentDAOOptional = Optional.empty();
		if (input.getJdLink() != null) {
			documentDAOOptional = this.documentRepository.findById(input.getJdLink());
		}
		final String partnerId = this.partnerConfigManager.getPartnerIdFromCompanyId(input.getCompanyId());

		if (!documentDAOOptional.isPresent()) {
			documentDAOOptional = this.documentRepository.findByFileUrl(input.getJdLink());
		}

		final String countryCode = input.getCountryCode() == null ? "IN" : input.getCountryCode();

		final List<String> hiringManagers = this.hiringManagerInfoSaver.manageHiringManagersInfo(jobRoleId,
				new HashSet<>(input.getHiringManagers() == null ? List.of() : input.getHiringManagers()));

		final List<String> recruiters = this.recruitersInfoSaver.manageRecruitersInfo(jobRoleId,
				new HashSet<>(input.getRecruiters() == null ? List.of() : input.getRecruiters()));

		final List<String> hiringTeamMembers = this.hiringTeamMemberInfoSaver.manageHiringTeamMemberInfo(jobRoleId,
				new HashSet<>(input.getHiringTeamMembers() == null ? List.of() : input.getHiringTeamMembers()));

		final Boolean isATSJobRole = input.getAtsStatusId() != null;

		final String pocEmail = input.getDefaultPocEmail() == null
				? (recruiters.isEmpty() ? null : this.getUserEmail(recruiters.get(0)))
				: input.getDefaultPocEmail();

		JobRoleDAO jobRoleDAO = JobRoleDAO.builder()
				.entityId(new VersionedEntityId(jobRoleId, jobRoleVersion))
				.category(input.getCategory())
				.companyId(input.getCompanyId())
				.partnerId(partnerId)
				.domainId(input.getDomainId())
				.remarks(input.getRemarks())
				.evaluationProcessType(input.getEvaluationProcessType())
				.jdLink(documentDAOOptional.isPresent() ? documentDAOOptional.get().getFileUrl() : input.getJdLink())
				.internalDisplayName(input.getInternalDisplayName())
				.candidateDisplayName(input.getCandidateDisplayName())
				.cutOffScore(input.getCutOffScore())
				.countryCode(countryCode)
				.eligibleCountriesOfExperts(
						input.getEligibleCountriesOfExperts() == null || input.getEligibleCountriesOfExperts().isEmpty()
								? Arrays.asList(countryCode)
								: input.getEligibleCountriesOfExperts())
				.timezone(input.getTimezone() == null ? "Asia/Kolkata" : input.getTimezone())
				.defaultPocEmail(pocEmail)
				.isDraft(Boolean.TRUE.equals(input.getIsDraft()))
				.activeCandidatesCountAggregate(
						this.jobRoleManager.getJobRoleActiveCandidateCountAggregate(jobRoleId, jobRoleVersion))
				.hiringManagers(hiringManagers)
				.recruiters(recruiters)
				.hiringTeamMembers(hiringTeamMembers)
				.teams(input.getTeams())
				.locations(input.getLocations())
				.extFullSync(input.getExtFullSync())
				.extFullSyncStatus(input.getExtFullSyncStatus())
				.atsStatus(input.getAtsStatusId())
				.creationSource(input.getCreationSource())
				.creationMeta(input.getCreationSourceMeta())
				.build();

		// Updating BR status
		jobRoleDAO = jobRoleDAO.toBuilder().brStatus(Boolean.TRUE.equals(isATSJobRole) ? List.of(input.getBrStatusId())
				: this.jobRoleStatusManager.getBrStatus(jobRoleDAO)).build();

		this.jobRoleManager.saveJobRole(jobRoleDAO);
		return jobRoleDAO;
	}

	public void saveRoundLevelInterviewStructure(final String jobRoleId, final Integer jobRoleVersion,
			final List<InterviewStructureInput> interviewStructureInputs) {
		this.interviewStructureRepository.saveAll(interviewStructureInputs.stream().map(
				interviewStructureInput -> InterviewStructureDAO.builder()
						.id(interviewStructureInput.getId())
						.name(interviewStructureInput.getName())
						.isBrRound(interviewStructureInput.getIsBrRound())
						.interviewFlowLink(interviewStructureInput.getInterviewFlowLink())
						.domainId(interviewStructureInput.getDomainId())
						.duration(interviewStructureInput.getDuration())
						.expertJoiningTime(interviewStructureInput.getExpertJoiningTime())
						.allSkillsFound(interviewStructureInput.getAllSkillsFound())
						.interviewFlow(interviewStructureInput.getInterviewFlow())
						.build())
				.collect(Collectors.toList()));

		this.saveJobRoleToInterviewStructure(interviewStructureInputs, jobRoleId, jobRoleVersion);

		this.saveInterviewStructureSkills(interviewStructureInputs);

		this.saveDefaultQuestions(interviewStructureInputs);
	}

	private void saveJobRoleToInterviewStructure(final List<InterviewStructureInput> interviewStructureInputs,
			final String jobRoleId, final Integer jobRoleVersion) {
		final ObjectMapper objectMapper = new ObjectMapper();
		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOs = new ArrayList<>();
		for (int i = 0; i < interviewStructureInputs.size(); ++i) {
			String categoryCutoffJSON = null;
			if (interviewStructureInputs.get(i).getCategoryCutoffs() != null) {
				try {
					categoryCutoffJSON = objectMapper
							.writeValueAsString(interviewStructureInputs.get(i).getCategoryCutoffs());
				} catch (Exception e) {

				}
			}
			jobRoleToInterviewStructureDAOs.add(
					JobRoleToInterviewStructureDAO.builder()
							.id(UUID.randomUUID().toString())
							.interviewStructureId(interviewStructureInputs.get(i).getId())
							.jobRoleId(jobRoleId)
							.jobRoleVersion(jobRoleVersion)
							.orderIndex(i)
							.interviewRound(interviewStructureInputs.get(i).getRound())
							.interviewStructureLink(interviewStructureInputs.get(i).getInterviewStructureLink())
							.acceptanceCutoffScore(interviewStructureInputs.get(i).getCutOffScore())
							.rejectionCutoffScore(interviewStructureInputs.get(i).getThresholdScore())
							.isManualActionForRemainingCases(interviewStructureInputs.get(i).getRequiresApproval())
							.recommendationScore(interviewStructureInputs.get(i).getRecommendationScore())
							.interviewCutoffScore(interviewStructureInputs.get(i).getInterviewCutoffScore())
							.categoryRejectionJSON(categoryCutoffJSON)
							.build());
		}
		this.jobRoleToInterviewStructureRepository.saveAll(jobRoleToInterviewStructureDAOs);
	}

	private void saveInterviewStructureSkills(final List<InterviewStructureInput> interviewStructureInputs) {
		this.interviewStructureSkillsRepository.saveAll(interviewStructureInputs.stream().map(
				interviewStructureInput -> interviewStructureInput.getCategoryIds().stream().map(
						category -> InterviewStructureSkillsDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewStructureId(interviewStructureInput.getId())
								.skillId(category)
								.isSpecific(false)
								.build())
						.collect(Collectors.toList()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));
		this.interviewStructureSkillsRepository.saveAll(interviewStructureInputs.stream().map(
				interviewStructureInput -> interviewStructureInput.getSpecificSkills().stream().map(
						skill -> InterviewStructureSkillsDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewStructureId(interviewStructureInput.getId())
								.skillId(skill.getId())
								.isSpecific(true)
								.isOptional(skill.getIsOptional())
								.build())
						.collect(Collectors.toList()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));
	}

	private void saveDefaultQuestions(final List<InterviewStructureInput> interviewStructureInputs) {
		this.defaultQuestionsRepository.saveAll(interviewStructureInputs.stream().map(
				interviewStructureInput -> interviewStructureInput.getDefaultQuestions().stream().map(
						question -> DefaultQuestionsDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewStructureId(interviewStructureInput.getId())
								.question(question)
								.build())
						.collect(Collectors.toList()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()));
	}

	public void saveSkillWeightages(final String jobRoleId, final Integer jobRoleVersion, final JobRoleInput input) {
		final List<SkillWeightageDAO> skillWeightageDAOS = input.getSkillWeightages().stream().map(
				skillWeightage -> SkillWeightageDAO.builder()
						.id(UUID.randomUUID().toString())
						.skillId(skillWeightage.getSkill().getId())
						.jobRoleId(jobRoleId)
						.jobRoleVersion(jobRoleVersion)
						.weightage(skillWeightage.getWeightage())
						.build())
				.collect(Collectors.toList());
		this.skillWeightageRepository.saveAll(skillWeightageDAOS);
	}

	private void saveJobRoleHistory(final JobRoleInput jobRoleInput, final String jobRoleId,
			final Integer jobRoleVersion) {
		this.jobRoleHistoryRepository.save(JobRoleHistoryDAO.builder().id(UUID.randomUUID().toString())
				.jobRoleId(jobRoleId).jobRoleRawState(jobRoleInput).build());
	}

	private List<String> checkIfExpertExistsForSpecificSkillsOrNot(
			final List<InterviewStructureInput> interviewStructures) {
		final List<String> errorsForNoAvailableExpertForSpecificSkills = new ArrayList<>();
		final List<SkillInput> specificSkills = interviewStructures.stream()
				.map(InterviewStructureInput::getSpecificSkills)
				.flatMap(Collection::stream).distinct().collect(Collectors.toList());
		final List<String> specificSkillIds = specificSkills.stream().map(SkillInput::getId)
				.collect(Collectors.toList());
		final List<String> specificSkillsForExperts = this.expertSkillsRepository.findAllBySkillIdIn(specificSkillIds)
				.stream().map(ExpertSkillsDAO::getSkillId).distinct().collect(Collectors.toList());
		specificSkillIds.removeAll(specificSkillsForExperts);
		if (specificSkillIds.size() > 0) {
			specificSkillIds.forEach(x -> errorsForNoAvailableExpertForSpecificSkills.add("No Expert found for " +
					"skill : " + specificSkills.stream()
							.filter(y -> y.getId().equals(x)).findFirst().get().getName()));
			return errorsForNoAvailableExpertForSpecificSkills;
		}
		return null;
	}

	private JobRoleCreationResult updateJobRoleBasedPricing(final String jobRoleId,
			final List<InterviewStructureInput> interviewStructureInputs, final String companyId, final String userId) {
		final JobRoleBasedPricingUpdationResult jobRoleBasedPricingUpdationResult = this.jobRolePricingDetailsUpdator
				.update(jobRoleId, interviewStructureInputs, companyId, userId);
		if (jobRoleBasedPricingUpdationResult.getValidationResult() != null
				&& jobRoleBasedPricingUpdationResult.getValidationResult().getFieldErrors().size() > 0) {
			final ArrayList<JobRoleCreationError> errors = (ArrayList<JobRoleCreationError>) jobRoleBasedPricingUpdationResult
					.getValidationResult().getFieldErrors().stream().map(x -> JobRoleCreationError.builder()
							.error(x.getMessage())
							.fieldTag(x.getFieldTag())
							.build())
					.collect(Collectors.toList());
			return JobRoleCreationResult.builder().success(Boolean.FALSE).errors(errors).type("error").build();
		}
		return null;
	}

	private void updateAtsMappings(final JobRoleInput jobRole, final String companyId) {
		final String partnerId = this.partnerConfigManager.getPartnerIdFromCompanyId(companyId);
		final UpdateAtsJobRoleMappingDTO.JobRoleMapping jobRoleMapping = UpdateAtsJobRoleMappingDTO.JobRoleMapping
				.builder()
				.jobRoleId(jobRole.getId())
				.atsJobPostingId(jobRole.getAtsId())
				.interviewStructureMappings(jobRole.getInterviewStructures().stream()
						.map(
								i -> UpdateAtsJobRoleMappingDTO.InterviewStructureMapping.builder()
										.interviewStructureId(i.getId())
										.atsInterviewStructureId(i.getAtsId())
										.build())
						.collect(Collectors.toList()))
				.build();
		final UpdateAtsJobRoleMappingDTO request = UpdateAtsJobRoleMappingDTO.builder()
				.partnerId(partnerId)
				.jobRoleMappings(List.of(jobRoleMapping))
				.build();
		this.atsServiceClient.updateJobRoleMappings(request);
	}

	private String getUserEmail(final String userId) {
		return this.userDetailsRepository.findById(userId).get().getEmail();
	}
}
