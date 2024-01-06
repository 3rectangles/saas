/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.utilities.EmailParser;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.common.graphql.types.AddBulkEvaluationsError;
import com.barraiser.common.graphql.types.AddBulkEvaluationsResult;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("addEvaluationDataValidationProcessor")
@AllArgsConstructor
public class DataValidationProcessor implements AddEvaluationProcessor {
	private final PhoneParser phoneParser;
	private final DomainRepository domainRepository;
	private final CompanyRepository companyRepository;
	private final EvaluationRepository evaluationRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final CandidateInformationManager candidateInformationManager;
	private final InterviewUtil interviewUtil;
	private final JobRoleRepository jobRoleRepository;
	private final EvaluationStatusManager evaluationStatusManager;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		final List<AddBulkEvaluationsError> errors = new ArrayList<>();

		if (data.getIsCandidateAnonymous() == null || !data.getIsCandidateAnonymous()) {
			this.validateCandidateInformation(data, errors);
		}

		if (!data.getIsAddedViaCalendarInterception()) {
			if (data.getIsAtsEvaluation() == null || !data.getIsAtsEvaluation()) {
				if (this.domainRepository.findById(data.getJobRoleDAO().getDomainId()).isEmpty()) {
					errors.add(AddBulkEvaluationsError.builder()
							.error(AddBulkEvaluationsMutation.GENERIC_ERROR_RESPONSE)
							.build());
				}
			}
		}

		if (!data.getIsAddedViaCalendarInterception()) {
			if (this.companyRepository.findById(data.getJobRoleDAO().getCompanyId()).isEmpty()) {
				errors.add(AddBulkEvaluationsError.builder()
						.error(AddBulkEvaluationsMutation.GENERIC_ERROR_RESPONSE)
						.build());
			}
		}

		if (!data.getIsAddedViaCalendarInterception()) {
			if (data.getForcedAddFlag() == null || !data.getForcedAddFlag()) {
				List<EvaluationDAO> alreadyFoundEvaluationDAOs = getAllJobRolesForACandidateNotCancelled(
						data.getJobRoleDAO().getPartnerId(), data.getEmail());
				if (alreadyFoundEvaluationDAOs != null && alreadyFoundEvaluationDAOs.size() > 0) {
					String roleStrings = "";
					for (EvaluationDAO evaluationDAO : alreadyFoundEvaluationDAOs) {
						String jobRoleID = evaluationDAO.getJobRoleId();
						JobRoleDAO jobRoleDAO = jobRoleRepository
								.findTopByEntityIdIdOrderByEntityIdVersionDesc(jobRoleID).get();
						final EvaluationDAO updatedEvaluationDAO = this.evaluationStatusManager
								.populateStatus(List.of((evaluationDAO)))
								.get(0);
						String roleString = jobRoleDAO.getInternalDisplayName() + "("
								+ updatedEvaluationDAO.getFinalStatus().getDisplayStatus()
								+ " - " + "<t:" + evaluationDAO.getCreatedOn().getEpochSecond() + ">)";
						if (roleStrings == "")
							roleStrings = roleString;
						else
							roleStrings = roleStrings + ", " + roleString;
					}
					roleStrings = roleStrings + ". <Upload Anyways>";
					errors.add(AddBulkEvaluationsError.builder()
							.error(
									"Already upoaded for: " + roleStrings)
							.build());
				}
			}
		}

		if (errors.size() > 0) {
			data.setResult(AddBulkEvaluationsResult.builder()
					.success(false)
					.errors(errors)
					.build());
		}
	}

	private void validateCandidateInformation(AddEvaluationProcessingData data, List<AddBulkEvaluationsError> errors) {
		try {
			EmailParser.validateEmail(data.getEmail());
		} catch (final Exception e) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Email not following proper format: " + data.getEmail())
					.fieldTag("email")
					.build());
		}

		final String parsedPhone = this.phoneParser.getFormattedPhone(data.getPhone());
		if (parsedPhone == null || !parsedPhone.equals(data.getPhone())) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Phone number not following proper format: " + data.getPhone())
					.fieldTag("phone")
					.build());
		}

		if (data.getCandidateName() == null || data.getCandidateName().isEmpty()) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Name cannot be empty")
					.fieldTag("name")
					.build());
		}

		if (data.getWorkExperience() == null) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Work Experience cannot be empty")
					.fieldTag("workExperience")
					.build());
		}

		if (data.getWorkExperience() < 0) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Work Experience cannot be negative")
					.fieldTag("workExperience")
					.build());
		}

		if (data.getResumeUrl() == null || data.getResumeUrl().isEmpty()) {
			errors.add(AddBulkEvaluationsError.builder()
					.error("Resume needs to be attached")
					.fieldTag("resumeUrl")
					.build());
		}
	}

	private Boolean isCandidateOnGoingForJobRole(final String email, final JobRoleDAO jobRole) {

		if (email != null) {
			final Optional<String> user = this.userInformationManagementHelper.findUserByEmail(email);
			if (user.isEmpty()) {
				return Boolean.FALSE;
			}

			final List<String> candidateIds = this.candidateInformationManager.getCandidatesByUserId(user.get())
					.stream()
					.map(CandidateDAO::getId).collect(Collectors.toList());

			final List<EvaluationDAO> onGoingEvaluations = this.evaluationRepository
					.findAllByCandidateIdInAndJobRoleIdAndStatusNotInAndDeletedOnIsNull(
							candidateIds, jobRole.getEntityId().getId(),
							List.of(EvaluationStatus.DONE.getValue(), EvaluationStatus.CANCELLED.getValue()));
			return onGoingEvaluations.size() > 0;
		}

		return Boolean.FALSE;
	}

	private List<EvaluationDAO> getAllJobRolesForACandidateNotCancelled(final String partnerId, final String email) {
		if (email != null) {
			final Optional<String> user = this.userInformationManagementHelper.findUserByEmail(email);
			if (user.isEmpty()) {
				return new ArrayList<EvaluationDAO>();
			}

			final List<String> candidateIds = this.candidateInformationManager.getCandidatesByUserId(user.get())
					.stream()
					.map(CandidateDAO::getId).collect(Collectors.toList());

			final List<EvaluationDAO> onGoingEvaluations = this.evaluationRepository
					.findAllByPartnerIdAndCandidateIdInAndStatusNotInAndDeletedOnIsNull(
							partnerId,
							candidateIds,
							List.of(EvaluationStatus.CANCELLED.getValue()));
			return onGoingEvaluations;
		}

		return new ArrayList<EvaluationDAO>();
	}
}
