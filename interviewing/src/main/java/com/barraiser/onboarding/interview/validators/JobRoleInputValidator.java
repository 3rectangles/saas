/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.validators;

import com.barraiser.common.utilities.EmailParser;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.common.graphql.input.InterviewStructureInput;
import com.barraiser.common.graphql.input.JobRoleInput;
import com.barraiser.common.graphql.input.SkillWeightageInput;
import com.barraiser.common.graphql.types.JobRoleCreationError;
import com.barraiser.onboarding.dal.SkillWeightageDAO;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JobRoleInputValidator {
	public ArrayList<JobRoleCreationError> validate(final JobRoleInput input) {

		ArrayList<JobRoleCreationError> errors = new ArrayList<>();

		if (Strings.isNullOrEmpty(input.getCompanyId())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("company")
					.error("No company present")
					.build());
		}
		try {
			EmailParser.validateEmail(input.getDefaultPocEmail());
		} catch (final Exception e) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("defaultPocEmail")
					.error("Default POC email is not valid")
					.build());
		}
		if (Strings.isNullOrEmpty(input.getDomainId())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("domain")
					.error("No Domain present")
					.build());
		}

		if (Strings.isNullOrEmpty(input.getCategory())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("category")
					.error("No category present")
					.build());
		}
		if (Strings.isNullOrEmpty(input.getInternalDisplayName())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("InternalDisplayName")
					.error("Job role name (internal) not mentioned")
					.build());
		}
		if (Strings.isNullOrEmpty(input.getCandidateDisplayName())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("CandidateDisplayName")
					.error("Job role name (candidate) not mentioned")
					.build());
		}
		if (Strings.isNullOrEmpty(input.getJdLink())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("jdLink")
					.error("Job Description File not uploaded")
					.build());
		}

		if (Strings.isNullOrEmpty(input.getCountryCode())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("countryCode")
					.error("country code not mentioned")
					.build());
		}

		if (Strings.isNullOrEmpty(input.getTimezone())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("timezone")
					.error("timezone not mentioned")
					.build());
		}

		input.getInterviewStructures()
				.forEach(interviewStructure -> errors.addAll(this.validateInterviewStructure(interviewStructure)));
		return errors;
	}

	public ArrayList<JobRoleCreationError> validateInterviewStructure(
			final InterviewStructureInput interviewStructure) {
		ArrayList<JobRoleCreationError> errors = new ArrayList<>();
		if (interviewStructure.getIsBrRound() == null) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("isBrRound")
					.error("isBrRound not mentioned")
					.build());
		}
		if (Boolean.TRUE.equals(interviewStructure.getIsBrRound())) {
			if (Strings.isNullOrEmpty(interviewStructure.getDomainId())) {
				errors.add(JobRoleCreationError.builder()
						.fieldTag("interviewStructureDomain")
						.error("Interview Structure domain not mentioned")
						.build());
			}
			if (Strings.isNullOrEmpty(interviewStructure.getRound())) {
				errors.add(JobRoleCreationError.builder()
						.fieldTag("interviewStructureRound")
						.error("Round not mentioned")
						.build());
			}
			if (interviewStructure.getExpertJoiningTime() == null ||
					interviewStructure.getExpertJoiningTime() >= interviewStructure.getDuration()) {
				errors.add(JobRoleCreationError.builder()
						.fieldTag("expertJoiningTime")
						.error("Expert joining time must be less than the Interview's duration")
						.build());
			}
		}
		if (interviewStructure.getCategoryIds().isEmpty()) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("interviewStructureCategories")
					.error("Atleast one category for interview structure need to be mentioned")
					.build());
		}
		if (interviewStructure.getDuration() == null) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("interviewStructureDuration")
					.error("Interview duration not mentioned")
					.build());
		}

		if (interviewStructure.getCutOffScore() != null && interviewStructure.getThresholdScore() == null) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("cutOffScore")
					.error("either select rejection score or manual approval")
					.build());
		}

		if (interviewStructure.getCutOffScore() == null && interviewStructure.getThresholdScore() != null) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("cutOffScore")
					.error("either select approval score or manual approval")
					.build());
		}

		if (interviewStructure.getCutOffScore() == null && interviewStructure.getThresholdScore() == null
				&& Boolean.TRUE.equals(interviewStructure.getRequiresApproval())) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("requiresApproval")
					.error("manual approval not required")
					.build());
		}

		if (interviewStructure.getCutOffScore() != null && interviewStructure.getThresholdScore() != null
				&& interviewStructure.getThresholdScore() > interviewStructure.getCutOffScore()) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("cutOffScore")
					.error("Rejection score cannot be greater than cutoff score")
					.build());
		}
		return errors;
	}

	public ArrayList<JobRoleCreationError> checkSize(final int size) {
		ArrayList<JobRoleCreationError> errors = new ArrayList<>();

		if (size == 0) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("rounds")
					.error("atleast one interview structure must be present")
					.build());
		}
		return errors;
	}

	public ArrayList<JobRoleCreationError> checkSkillWeightageSumAndIndividualWeightage(
			final List<SkillWeightageInput> skillWeightageInputs, List<InterviewStructureInput> interviewStructures) {
		ArrayList<JobRoleCreationError> errors = new ArrayList<>();

		final List<String> skillsToBePresent = interviewStructures.stream()
				.map(InterviewStructureInput::getCategoryIds)
				.flatMap(Collection::stream)
				.filter(s -> !Constants.OTHERS_SKILL_ID.equals(s))
				.distinct()
				.collect(Collectors.toList());
		// to handle old jobrole edits(where softskills where special case)
		boolean containsSoftSkill = skillWeightageInputs.stream()
				.anyMatch(input -> input.getSkill().getId().equals(Constants.SOFT_SKILL_ID));
		if (containsSoftSkill)
			skillsToBePresent.add(Constants.SOFT_SKILL_ID);
		if (skillsToBePresent.size() != skillWeightageInputs.size()) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("skill weightage")
					.error("skills having weightages do not completely match with that of the interview structures")
					.build());
			return errors;
		}

		double sum = 0;
		for (SkillWeightageInput skillWeightageInput : skillWeightageInputs) {
			if (skillWeightageInput.getWeightage() != null && skillWeightageInput.getWeightage() >= 0
					&& skillWeightageInput.getWeightage() <= 100) {
				sum += skillWeightageInput.getWeightage();
			} else {
				errors.add(JobRoleCreationError.builder()
						.fieldTag("skill weightage")
						.error("skill weightage not between 0 and 100")
						.build());
				break;
			}
			if (!skillsToBePresent.contains(skillWeightageInput.getSkill().getId())) {
				errors.add(JobRoleCreationError.builder()
						.fieldTag("skill weightage")
						.error("skills having weightages do not completely match with that of the interview structures")
						.build());
				return errors;
			}
		}

		if (!this.containsOnlySoftSkills(skillWeightageInputs) && sum != 100) {
			errors.add(JobRoleCreationError.builder()
					.fieldTag("skill weightage")
					.error("sum of skill weightages is not equal to 100")
					.build());
		}

		return errors;
	}

	public Boolean containsOnlySoftSkills(final List<SkillWeightageInput> skillWeightages) {
		return ((skillWeightages.size() == 1)
				&& Constants.SOFT_SKILL_ID.equals(skillWeightages.get(0).getSkill().getId()));
	}
}
