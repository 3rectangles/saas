/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.common.graphql.input.UpdateCandidatureInput;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.add_evaluation.DatabaseUserManagementProcessor;
import com.barraiser.onboarding.user.candidate.CandidateUpdator;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class UpdateCandidatureMutation implements NamedDataFetcher<Object> {

	private static final String EMAIL = "email";
	private static final String PHONE = "phone";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String DESIGNATION = "designation";
	private static final String ALMA_MATER = "almaMater";
	private static final String CURRENT_COMPANY = "currentCompany";
	private static final String WORK_EXPERIENCE = "workExperience";
	private static final String TIMEZONE = "timezone";
	private static final String EVALUATION = "EVALUATION";

	private final GraphQLUtil graphQLUtil;
	private final EvaluationRepository evaluationRepository;
	private final DocumentRepository documentRepository;
	private final CandidateUpdator candidateUpdator;
	private final CandidateInformationManager candidateInformationManager;
	private final UserDetailsSnapShotRepository userDetailsSnapShotRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final CandidatureUpdaterInJira candidatureUpdaterInJira;
	private final DatabaseUserManagementProcessor databaseUserManagementProcessor;

	@Override
	public String name() {
		return "updateCandidature";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final UpdateCandidatureInput input = this.graphQLUtil.getInput(environment, UpdateCandidatureInput.class);
		final String evaluationId = input.getEvaluationId();
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository.findById(evaluationId);

		final CandidateDAO candidateDAO = this.candidateInformationManager
				.getCandidate(evaluationDAO.get().getCandidateId());

		final Boolean result = this.updateCandidatureDetails(candidateDAO, evaluationDAO.get(), input);

		return DataFetcherResult.newResult().data(result).build();
	}

	private Boolean updateCandidatureDetails(final CandidateDAO candidateDAO, final EvaluationDAO evaluationDAO,
			final UpdateCandidatureInput candidatureDetailsInput) {
		final UserDetailsInput detailsToBeUpdated = candidatureDetailsInput
				.getCandidatureDetailsToBeUpdated().getUserDetailsInput();
		final Map<String, Object> userFieldsToUpdate = new HashMap<>();
		final Boolean isCandidateAnonymous = this.candidateInformationManager
				.isCandidateAnonymous(evaluationDAO.getCandidateId());

		if (isCandidateAnonymous) {
			log.error("cannot sync for user mapped to candidate {} details for user as they are anonymous",
					candidateDAO.getId());
			return false;
		}

		final String candidateUserId = this.getUserIdOfCandidate(candidateDAO, detailsToBeUpdated.getEmail());

		final UserDetailsDAO candidateAccessUserDAO = this.userInformationManagementHelper
				.findUserById(candidateUserId);

		this.createUserFieldsMap(userFieldsToUpdate, detailsToBeUpdated, candidateDAO,
				candidateAccessUserDAO);
		if (candidatureDetailsInput.getCandidatureDetailsToBeUpdated().getPocEmail() != null)
			this.updatePocEmail(evaluationDAO,
					candidatureDetailsInput.getCandidatureDetailsToBeUpdated().getPocEmail());

		this.candidateUpdator.updateFields(candidateDAO.getId(), candidateAccessUserDAO.getId(), userFieldsToUpdate);
		this.candidatureUpdaterInJira.updateUserFieldsInJira(candidatureDetailsInput);
		if (detailsToBeUpdated.getDocumentId() != null) {
			this.updateDocumentDetails(detailsToBeUpdated.getDocumentId(), candidateDAO);
		}
		this.saveCandidatureSnapShot(evaluationDAO);
		return true;
	}

	private void createUserFieldsMap(Map<String, Object> userFieldsToUpdate,
			UserDetailsInput detailsToBeUpdated,
			final CandidateDAO candidateDAO,
			final UserDetailsDAO candidateAccessUserDAO) {

		userFieldsToUpdate.put(EMAIL,
				detailsToBeUpdated.getEmail() != null ? detailsToBeUpdated.getEmail()
						: candidateAccessUserDAO.getEmail());
		userFieldsToUpdate.put(PHONE,
				detailsToBeUpdated.getPhone() != null ? detailsToBeUpdated.getPhone()
						: candidateAccessUserDAO.getPhone());

		userFieldsToUpdate.put(FIRST_NAME,
				detailsToBeUpdated.getFullName() != null ? detailsToBeUpdated.getFullName().split("\\s+", 2)[0]
						: candidateDAO.getFirstName());
		userFieldsToUpdate.put(LAST_NAME,
				detailsToBeUpdated.getFullName().split("\\s+", 2).length > 1
						? detailsToBeUpdated.getFullName().split("\\s+", 2)[1]
						: candidateDAO.getLastName());
		userFieldsToUpdate.put(DESIGNATION,
				detailsToBeUpdated.getDesignation() != null ? detailsToBeUpdated.getDesignation()
						: candidateDAO.getDesignation());
		userFieldsToUpdate.put(ALMA_MATER,
				detailsToBeUpdated.getAlmaMater() != null ? detailsToBeUpdated.getAlmaMater()
						: candidateDAO.getAlmaMater());
		userFieldsToUpdate.put(CURRENT_COMPANY,
				detailsToBeUpdated.getCurrentCompanyName() != null ? detailsToBeUpdated.getCurrentCompanyName()
						: candidateDAO.getCurrentCompanyName());
		userFieldsToUpdate.put(WORK_EXPERIENCE,
				detailsToBeUpdated.getWorkExperienceInMonths() != null ? detailsToBeUpdated.getWorkExperienceInMonths()
						: candidateDAO.getWorkExperienceInMonths());
		userFieldsToUpdate.put(TIMEZONE, detailsToBeUpdated.getTimezone() != null ? detailsToBeUpdated.getTimezone()
				: candidateDAO.getTimezone());
	}

	private void updateDocumentDetails(String documentId, CandidateDAO candidateDAO) {
		final Optional<DocumentDAO> documentDAO = this.documentRepository.findById(documentId);
		documentDAO.ifPresent(dao -> {
			this.candidateInformationManager.updateCandidate(candidateDAO.toBuilder()
					.resumeUrl(dao.getFileUrl())
					.redactedResumeUrl(dao.getFileUrl())
					.resumeId(documentId)
					.build());
			this.databaseUserManagementProcessor.sendRedactedResumeEvent(dao.getFileUrl(), candidateDAO.getId());
		});
	}

	private void updatePocEmail(final EvaluationDAO evaluationDAO, final String pocEmails) {
		this.evaluationRepository
				.save(evaluationDAO.toBuilder().pocEmail(String.join(", ", pocEmails)).build());
	}

	private void saveCandidatureSnapShot(final EvaluationDAO evaluationDAO) {
		final CandidateDAO updatedCandidateDAO = this.candidateInformationManager
				.getCandidate(evaluationDAO.getCandidateId());
		final UserDetailsSnapShotDAO userDetailsSnapShotDAO = UserDetailsSnapShotDAO.builder()
				.id(UUID.randomUUID().toString())
				.entityType(EVALUATION)
				.entityId(evaluationDAO.getId())
				.payload(updatedCandidateDAO)
				.build();
		this.userDetailsSnapShotRepository.save(userDetailsSnapShotDAO);
	}

	private String getUserIdOfCandidate(final CandidateDAO candidateDAO, final String userEmail) {

		final String userIdInCognito = this.userInformationManagementHelper.findUserByEmail(userEmail).orElse(null);
		if (userIdInCognito != null) {
			return userIdInCognito;
		}
		return candidateDAO.getUserId();
	}
}
