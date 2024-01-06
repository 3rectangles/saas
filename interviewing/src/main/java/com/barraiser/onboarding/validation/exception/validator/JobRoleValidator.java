/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.validation.exception.validator;

import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.ValidationResult;

import com.barraiser.commons.dto.jobRoleManagement.InterviewStructureInput;
import com.barraiser.commons.dto.jobRoleManagement.JobRoleInput;
import com.barraiser.commons.dto.jobRoleManagement.SkillWeightageInput;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class JobRoleValidator implements DataValidator<JobRoleInput> {

	@Override
	public ValidationResult validate(final JobRoleInput input) {
		final ValidationResult validationResult = new ValidationResult();

		this.performBasicFieldValidation(input, validationResult);

		if (validationResult.getFieldErrors().isEmpty()) {
			this.performOverallValidation(input, validationResult);
		}

		return validationResult;
	}

	private void performBasicFieldValidation(
			final JobRoleInput input, final ValidationResult validationResult) {

		final List<FieldValidationResult> fieldErrors = new ArrayList<>();

		// TBD-SC: Field tags to be put based on FE , Thinking we can use ids rather
		// than field name
		if (input.getPartnerId() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Partner id value cannot be empty.")
							.build());
		}

		if (input.getDomainId() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Domain value cannot be empty.")
							.build());
		}

		if (input.getCategory() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Category  value cannot be empty.")
							.build());
		}

		if (input.getInternalDisplayName() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Internal display name value cannot be empty.")
							.build());
		}

		if (input.getCandidateDisplayName() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Candidate display name value cannot be empty.")
							.build());
		}

		if (input.getEvaluationProcessType() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Evaluation process type value cannot be empty.")
							.build());
		}

		if (input.getJdLink() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Job Description File not uploaded.")
							.build());
		}

		if (input.getInterviewStructures() == null || input.getInterviewStructures().size() == 0) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Atleast one interview structure must be present")
							.build());
		}

		this.performFieldValidationForSkillWeightages(input, fieldErrors);

		this.performFieldValidationForInterviewStructures(
				input.getInterviewStructures(), fieldErrors);

		validationResult.setFieldErrors(fieldErrors);
	}

	private void performFieldValidationForSkillWeightages(
			JobRoleInput input, final List<FieldValidationResult> fieldErrors) {
		if (this.validateSkillWeightages(input.getSkillWeightages())) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message(
									"Skill Weightages can not be empty and values should be between"
											+ " 0 and 100.")
							.build());
		}

		if (input.getSkillWeightages().stream().mapToDouble(SkillWeightageInput::getWeightage).sum() != 100) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message("Skills Weightages should add up to 100.")
							.build());
		}
	}

	private void performFieldValidationForInterviewStructures(
			final List<InterviewStructureInput> interviewStructures,
			final List<FieldValidationResult> fieldErrors) {
		interviewStructures.forEach(
				interviewStructure -> {
					if (interviewStructure.getRound() == null) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message("Interview Structure Round cannot be empty.")
										.build());
					}

					if (interviewStructure.getRoundIndex() == null) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message("Interview Structure Round index cannot be empty.")
										.build());
					}

					if (interviewStructure.getDuration() == null) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message("Interview Structure duration cannot be empty.")
										.build());
					}

					if (interviewStructure.getCategoryIds() == null
							|| interviewStructure.getCategoryIds().size() == 0) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message(
												"Atleast one category for interview structure need"
														+ " to be mentioned.")
										.build());
					}

					if (interviewStructure.getExpertJoiningTime() == null) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message(
												"Interview structure Expert joining value cannot be"
														+ " empty.")
										.build());
					}

					if (interviewStructure.getDuration() != null
							&& interviewStructure.getExpertJoiningTime() > interviewStructure.getDuration()) {
						fieldErrors.add(
								FieldValidationResult.builder()
										.fieldTag("")
										.message(
												"In an interview structure, Expert joining time"
														+ " should be less than interview duration.")
										.build());
					}
					this.validateCriteriaFields(fieldErrors, interviewStructure);
				});
	}

	private void validateCriteriaFields(
			final List<FieldValidationResult> fieldErrors,
			final InterviewStructureInput interviewStructure) {
		if (interviewStructure.getRoundClearanceCriteria() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message(
									"Interview structure round clearance criteria input value"
											+ " cannot be empty.")
							.build());
		}
		if (interviewStructure.getRequiresApproval() == null) {
			fieldErrors.add(
					FieldValidationResult.builder()
							.fieldTag("")
							.message(
									"Interview structure round clearance criteria input, isManual"
											+ " flag value cannot be empty.")
							.build());
		}
	}

	private boolean validateSkillWeightages(final List<SkillWeightageInput> skillWeightages) {
		if (skillWeightages == null || skillWeightages.size() == 0) {
			return true;
		}

		for (final SkillWeightageInput skillWeightage : skillWeightages) {
			if (skillWeightage.getWeightage() < 0 || skillWeightage.getWeightage() > 100) {
				return true;
			}
		}
		return false;
	}

	private void performOverallValidation(
			final JobRoleInput input, final ValidationResult validationResult) {

		final List<String> overallErrors = new ArrayList<>();

		input.getInterviewStructures()
				.forEach(
						is -> {
							if (is.getRoundClearanceCriteria().getApprovalCriteria() != null
									&& is.getRoundClearanceCriteria().getRejectionCriteria() != null
									&& is.getRequiresApproval() == false) {
								overallErrors.add(
										"Interview structure requires approval should be true if"
												+ " both approval and rejection criteria are provided");
							}
						});

		validationResult.setOverallErrors(overallErrors);
	}

	@Override
	public String type() {
		return "";
	}
}
