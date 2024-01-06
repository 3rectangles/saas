/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.duplicateInterview;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.SkillWeightageManager;
import com.barraiser.onboarding.media.CopyInterviewVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
public class CreateDuplicateInterviewForExpertTraining {
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final QuestionRepository questionRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final InterviewService interviewService;
	private final CopyInterviewVideoService copyInterviewVideoService;
	private final SkillWeightageManager skillWeightageManager;

	@PostMapping(path = "/create-duplicate-interview", consumes = "application/json", produces = "application/json")
	@Transactional
	public ResponseEntity<InterviewDAO> duplicateInterviewForTraining(
			@RequestBody final CreateDuplicateInterviewRequestDTO input) {
		this.validateInput(input);

		final InterviewDAO originalInterview = this.interViewRepository
				.findById(input.getInterviewId())
				.orElseThrow(() -> new IllegalArgumentException("Interview not found"));
		final EvaluationDAO originalEvaluation = this.evaluationRepository
				.findById(originalInterview.getEvaluationId())
				.orElseThrow(() -> new IllegalArgumentException("Evaluation not found"));
		final JobRoleDAO originalJobRole = this.jobRoleManager
				.getJobRoleFromEvaluation(originalEvaluation)
				.orElseThrow(() -> new IllegalArgumentException("Job role not found"));
		final JobRoleDAO duplicateJobRole = this.createDuplicateJobRole(originalJobRole, input,
				originalEvaluation.getId());
		final EvaluationDAO duplicateEvaluation = this.createDuplicateEvaluation(originalEvaluation, input,
				duplicateJobRole);
		final InterviewDAO duplicateInterview = this.createDuplicateInterview(originalInterview, input,
				duplicateEvaluation);
		this.createDuplicateQuestions(originalInterview, duplicateInterview);

		this.copyInterviewVideoService.copyInterviewVideo(input.getInterviewId(), duplicateInterview.getId());

		return ResponseEntity.ok(duplicateInterview);
	}

	private JobRoleDAO createDuplicateJobRole(
			final JobRoleDAO originalJobRole,
			final CreateDuplicateInterviewRequestDTO input,
			final String evaluationId) {
		if (originalJobRole.getCompanyId().equals(input.getTestCompanyId())) {
			return originalJobRole;
		}
		final JobRoleDAO duplicateJobRole = originalJobRole.toBuilder()
				.entityId(new VersionedEntityId(UUID.randomUUID().toString(), 0))
				.internalDisplayName(
						originalJobRole.getInternalDisplayName() + "_test_copy")
				.companyId(input.getTestCompanyId())
				.activeCandidatesCountAggregate(0L)
				.build();
		this.jobRoleManager.saveJobRole(duplicateJobRole);

		final List<JobRoleToInterviewStructureDAO> originalJobRoleToInterviewStructures = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						originalJobRole.getEntityId().getId(),
						originalJobRole.getEntityId().getVersion());
		final List<JobRoleToInterviewStructureDAO> duplicateJobRoleToInterviewStructures = originalJobRoleToInterviewStructures
				.stream()
				.map(
						o -> o.toBuilder()
								.id(UUID.randomUUID().toString())
								.jobRoleId(duplicateJobRole.getEntityId().getId())
								.jobRoleVersion(
										duplicateJobRole.getEntityId().getVersion())
								.build())
				.collect(Collectors.toList());
		this.jobRoleToInterviewStructureRepository.saveAll(duplicateJobRoleToInterviewStructures);

		final List<SkillWeightageDAO> originalSkillWeightages = this.skillWeightageManager
				.getSkillWeightageForEvaluation(evaluationId);
		final List<SkillWeightageDAO> duplicateSkillWeightages = originalSkillWeightages.stream()
				.map(
						o -> o.toBuilder()
								.id(UUID.randomUUID().toString())
								.jobRoleId(duplicateJobRole.getEntityId().getId())
								.jobRoleVersion(
										duplicateJobRole.getEntityId().getVersion())
								.evaluationId(null)
								.build())
				.collect(Collectors.toList());
		this.skillWeightageManager.saveAll(duplicateSkillWeightages);

		return duplicateJobRole;
	}

	private EvaluationDAO createDuplicateEvaluation(
			final EvaluationDAO originalEvaluation,
			final CreateDuplicateInterviewRequestDTO input,
			final JobRoleDAO duplicateJobRole) {
		final UserDetailsDAO expertDetails = this.userDetailsRepository.findById(input.getExpertId()).get();
		final EvaluationDAO duplicateEvaluation = originalEvaluation.toBuilder()
				.id(UUID.randomUUID().toString())
				.candidateId(input.getTestCandidateId())
				.jobRoleId(duplicateJobRole.getEntityId().getId())
				.jobRoleVersion(duplicateJobRole.getEntityId().getVersion())
				.status(EvaluationStatus.DONE.getValue())
				.createdBy("duplicate_interview_system")
				.companyId(input.getTestCompanyId())
				.pocEmail(
						expertDetails.getFirstName()
								+ "_"
								+ expertDetails.getLastName()
								+ "@brduplicateinterview.com")
				.build();
		this.evaluationRepository.save(duplicateEvaluation);
		return duplicateEvaluation;
	}

	private InterviewDAO createDuplicateInterview(
			final InterviewDAO originalInterview,
			final CreateDuplicateInterviewRequestDTO input,
			final EvaluationDAO duplicateEvaluation) {
		InterviewDAO duplicateInterview = originalInterview.toBuilder()
				.id(UUID.randomUUID().toString())
				.interviewerId(input.getExpertId())
				.intervieweeId(input.getTestCandidateId())
				.status(InterviewStatus.DONE.getValue())
				.evaluationId(duplicateEvaluation.getId())
				.duplicateReason(input.getDuplicateReason())
				.build();
		duplicateInterview = this.interviewService.save(duplicateInterview);
		return duplicateInterview;
	}

	private void createDuplicateQuestions(
			final InterviewDAO originalInterview, final InterviewDAO duplicateInterview) {
		final List<QuestionDAO> originalQuestions = this.questionRepository
				.findAllByInterviewId(originalInterview.getId());
		final Map<String, String> idMap = new HashedMap();
		final List<QuestionDAO> duplicateQuestions = originalQuestions.stream()
				.map(
						o -> {
							final String newId = UUID.randomUUID().toString();
							idMap.put(o.getId(), newId);
							return o.toBuilder()
									.id(newId)
									.interviewId(duplicateInterview.getId())
									.type(null)
									.masterQuestionId(null)
									.build();
						})
				.collect(Collectors.toList());
		final List<QuestionDAO> duplicateQuestionsWithMasterId = new ArrayList<>();
		final int[] i = new int[1];
		for (i[0] = 0; i[0] < duplicateQuestions.size(); ++i[0]) {
			duplicateQuestionsWithMasterId.add(
					duplicateQuestions.get(i[0]).toBuilder()
							.masterQuestionId(
									duplicateQuestions.stream()
											.filter(
													d -> d.getId()
															.equals(
																	idMap.getOrDefault(
																			originalQuestions
																					.get(
																							i[0])
																					.getMasterQuestionId(),
																			"")))
											.findFirst()
											.orElse(QuestionDAO.builder().build())
											.getId())
							.build());
		}
		this.questionRepository.saveAll(duplicateQuestionsWithMasterId);
	}

	private void validateInput(final CreateDuplicateInterviewRequestDTO input) {
		if (input.getInterviewId() == null
				|| input.getExpertId() == null
				|| input.getTestCandidateId() == null
				|| input.getTestCompanyId() == null
				|| input.getDuplicateReason() == null) {
			throw new IllegalArgumentException();
		}
	}
}
