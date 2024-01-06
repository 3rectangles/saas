/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.addevaluationevent.AddEvaluationEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.atserrorevent.ATSErrorEvent;
import com.barraiser.commons.eventing.schema.commons.AuthenticatedUser;
import com.barraiser.onboarding.auth.apikey.ApiKeyDAO;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.graphql.GenerateEventMutation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class GreenhouseSendTestRequestHandler {
	private static final String SOURCE = "GREENHOUSE";
	private static final String SEND_TEST_FAILURE_ERROR_CODE = "SEND_TEST_FAILURE";
	private static final String SEND_TEST_SOURCE = "SEND_TEST_SOURCE";
	private static final String JOB_ROLE = "JOB_ROLE";

	private static final String ADD_EVALUATION_EVENT = "AddEvaluationEvent";

	private final GreenhouseRepository greenhouseRepository;
	private final PhoneParser phoneParser;
	private final GenerateEventMutation generateEventMutation;
	private final UserDetailsRepository userDetailsRepository;

	public String addCandidateForEvaluation(
			final GreenhouseSendTestRequestBody greenhouseSendTestRequestBody,
			final JobRoleDAO jobRoleDAO,
			final UserDetailsDAO userDetailsDAO,
			final ApiKeyDAO apiKeyDAO)
			throws Exception {
		log.info(
				String.format(
						"GreenhouseSendTestRequestHandler for jobRoledId : %s by UserId : %s"
								+ " called",
						jobRoleDAO.getEntityId(), userDetailsDAO.getId()));

		final AuthenticatedUser authenticatedUserForEvent = this.getAuthenticatedUser(
				apiKeyDAO,
				userDetailsDAO);

		final String candidateName = this.getCandidateName(greenhouseSendTestRequestBody);

		final AddEvaluationEvent addEvaluationEvent = this.constructAddEvaluationEvent(
				candidateName,
				greenhouseSendTestRequestBody,
				jobRoleDAO,
				authenticatedUserForEvent);

		this.generateEventMutation
				.pushEvent(
						ADD_EVALUATION_EVENT,
						addEvaluationEvent);

		this.saveGreenhouseDataInDatabase(
				greenhouseSendTestRequestBody,
				addEvaluationEvent);

		return addEvaluationEvent.getEvaluationId();
	}

	private AuthenticatedUser getAuthenticatedUser(
			final ApiKeyDAO apiKeyDAO,
			final UserDetailsDAO userDetailsDAO) {
		List<String> userRoles = new ArrayList<>(apiKeyDAO.getRoles());

		return new AuthenticatedUser()
				.userName(userDetailsDAO.getId())
				.email(userDetailsDAO.getEmail())
				.userRole(userRoles);
	}

	private String getCandidateName(final GreenhouseSendTestRequestBody greenhouseSendTestRequestBody) {
		return greenhouseSendTestRequestBody.getCandidate().getFirstName()
				+ " "
				+ greenhouseSendTestRequestBody.getCandidate().getLastName();
	}

	private AddEvaluationEvent constructAddEvaluationEvent(
			final String candidateName,
			final GreenhouseSendTestRequestBody greenhouseSendTestRequestBody,
			final JobRoleDAO jobRoleDAO,
			final AuthenticatedUser authenticatedUserForEvent) {
		return new AddEvaluationEvent()
				.evaluationId(
						UUID
								.randomUUID()
								.toString())
				.candidateName(candidateName)
				.documentLink(
						greenhouseSendTestRequestBody
								.getCandidate()
								.getResumeUrl())
				.jobRoleId(
						jobRoleDAO
								.getEntityId()
								.getId())
				.partnerId(jobRoleDAO.getPartnerId())
				.phone(this.phoneParser
						.getFormattedPhone(greenhouseSendTestRequestBody.getCandidate().getPhoneNumber()))
				.email(
						greenhouseSendTestRequestBody
								.getCandidate()
								.getEmail())
				.pocEmail(this.getUserEmail(jobRoleDAO.getRecruiters().get(0)))
				.source(SOURCE)
				.authenticatedUser(authenticatedUserForEvent);
	}

	private void saveGreenhouseDataInDatabase(
			final GreenhouseSendTestRequestBody greenhouseSendTestRequestBody,
			final AddEvaluationEvent addEvaluationEvent) {
		this.greenhouseRepository.save(
				GreenhouseDAO.builder()
						.id(
								UUID
										.randomUUID()
										.toString())
						.evaluationId(addEvaluationEvent.getEvaluationId())
						.profileUrl(
								greenhouseSendTestRequestBody
										.getCandidate()
										.getGreenhouseProfileUrl())
						.updateStatusUrl(greenhouseSendTestRequestBody.getUrl())
						.build());
	}

	public Boolean checkMandatoryRequirements(
			final ApiKeyDAO apiKeyDAO,
			final GreenhouseSendTestRequestBody sendTestRequestBody)
			throws Exception {
		return (sendTestRequestBody.getCandidate().getResumeUrl() != null)
				&& (sendTestRequestBody.getCandidate().getEmail() != null)
				&& (!sendTestRequestBody.getCandidate().getEmail().equals(""))
				&& (sendTestRequestBody.getCandidate().getPhoneNumber() != null)
				&& (!sendTestRequestBody.getCandidate().getPhoneNumber().equals(""))
				&& (this.phoneParser.getFormattedPhone(sendTestRequestBody.getCandidate().getPhoneNumber()) != null);
	}

	public ATSErrorEvent constructATSErrorEvent(
			final GreenhouseSendTestRequestBody sendTestRequestBody,
			final ApiKeyDAO apiKeyDAO) {
		Map<String, Object> errorPayloadMap = new HashMap<>();

		errorPayloadMap.put(
				"candidateName",
				String.format(
						"%s %s",
						sendTestRequestBody.getCandidate().getFirstName(),
						sendTestRequestBody.getCandidate().getLastName()));

		final List<String> missingInformation = this.getMissingInformationFromRequestBody(sendTestRequestBody);

		errorPayloadMap.put(
				"missingInformation",
				missingInformation);

		errorPayloadMap.put(
				"atsCandidateProfileURL",
				sendTestRequestBody
						.getCandidate()
						.getGreenhouseProfileUrl());

		return new ATSErrorEvent()
				.atsProvider(SOURCE)
				.errorCode(SEND_TEST_FAILURE_ERROR_CODE)
				.errorPayload(errorPayloadMap)
				.source(SEND_TEST_SOURCE)
				.partnerId(apiKeyDAO.getPartnerId())
				.entityId(sendTestRequestBody.getPartnerTestId())
				.entityType(JOB_ROLE);
	}

	private List<String> getMissingInformationFromRequestBody(final GreenhouseSendTestRequestBody sendTestRequestBody) {
		List<String> missingInformation = new ArrayList<>();

		if (sendTestRequestBody.getCandidate().getEmail() == null
				|| sendTestRequestBody.getCandidate().getEmail().equals("")) {
			missingInformation.add("Candidate email");
		}

		if (sendTestRequestBody.getCandidate().getPhoneNumber() == null
				|| (sendTestRequestBody.getCandidate().getPhoneNumber().equals(""))
				|| (this.phoneParser.getFormattedPhone(sendTestRequestBody.getCandidate().getPhoneNumber()) == null)) {
			missingInformation.add(
					"Candidate mobile number (Make sure to include the correct country code in the Ph. No. for eg. +91 for India)");
		}

		if (sendTestRequestBody.getCandidate().getResumeUrl() == null) {
			missingInformation.add("Candidate's Resume");
		}

		return missingInformation;
	}

	private String getUserEmail(final String userId) {
		return this.userDetailsRepository.findById(userId).get().getEmail();
	}
}
