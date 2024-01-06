/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.graphql.types.AddBulkEvaluationsResult;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.addevaluationevent.AddEvaluationEvent;
import com.barraiser.onboarding.communication.ErrorCommunication;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.resume.ParsedResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class AddEvaluationEventHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final AddEvaluation addEvaluation;
	private final JobRoleRepository jobRoleRepository;
	private final ResumeUrlProcessor resumeUrlProcessor;
	private final ParsedResumeRepository parsedResumeRepository;
	private final ErrorCommunication errorCommunication;
	private final PhoneParser phoneParser;
	private final InterviewUtil interviewUtil;

	@Override
	public List<Class> eventsToListen() {
		return List.of(AddEvaluationEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final AddEvaluationEvent addEvaluationEvent = this.objectMapper.convertValue(
				event.getPayload(),
				AddEvaluationEvent.class);

		log.info("AddEvaluation received in AddEvaluationEventHandler");

		String documentId = null;
		try {
			documentId = this.getDocumentIdFromExternalResumeLink(addEvaluationEvent);
		} catch (Exception e) {
			log.error("Unable to upload resume : ", e);
			throw e;
		}

		Integer workExperience;
		try {
			workExperience = this.getWorkExperience(documentId);
		} catch (Exception e) {
			log.error("Unable to get work experience : ", e);
			throw e;
		}

		final AddEvaluationProcessingData data = AddEvaluationProcessingData.builder()
				.evaluationId(addEvaluationEvent.getEvaluationId())
				.candidateName(addEvaluationEvent.getCandidateName())
				.phone(this.phoneParser.getFormattedPhone(addEvaluationEvent.getPhone()))
				.email(addEvaluationEvent.getEmail())
				.workExperience(workExperience)
				.jobRoleDAO(this.jobRoleRepository
						.findTopByEntityIdIdOrderByEntityIdVersionDesc(addEvaluationEvent.getJobRoleId()).get())
				.pocEmail(addEvaluationEvent.getPocEmail())
				.documentId(documentId)
				.documentLink(addEvaluationEvent.getDocumentLink())
				.resumeUrl(this.getResumeUrl(addEvaluationEvent, documentId))
				.authenticatedUser(this.getAuthenticatedUser(addEvaluationEvent))
				.isAddedViaCalendarInterception(this.interviewUtil
						.isAddedViaCalInterception(addEvaluationEvent.getSource()))
				.build();

		data.setResult(AddBulkEvaluationsResult.builder().success(true).build());

		try {
			this.addEvaluation.add(data);
			if (!data.getResult().getSuccess()) {
				throw new IllegalArgumentException();
			}
		} catch (Exception exception) {
			log.error("Unable to add evaluation : ", exception);
			String candidateInformation = "" + "Name : ";
			candidateInformation += addEvaluationEvent.getCandidateName();
			candidateInformation += "\nPhone : ";
			candidateInformation += addEvaluationEvent.getPhone();
			candidateInformation += "\nEmail : ";
			candidateInformation += addEvaluationEvent.getEmail();
			this.errorCommunication.sendEvaluationAdditionFailureEmailToPOC(
					"Unable to add evaluation of candidate : " + addEvaluationEvent.getCandidateName(),
					candidateInformation,
					data.getPocEmail());
			throw exception;
		}
	}

	private String getDocumentIdFromExternalResumeLink(AddEvaluationEvent addEvaluationEvent) throws Exception {
		return this.resumeUrlProcessor
				.getDocumentIdFromExternalResumeLink(
						addEvaluationEvent.getAuthenticatedUser().getUserName(),
						addEvaluationEvent.getDocumentLink(),
						addEvaluationEvent.getSource());
	}

	private Integer getWorkExperience(String documentId) {
		return this.parsedResumeRepository.findByDocumentId(documentId).getExperienceInMonths();
	}

	private List<UserRole> getUserRoles(AddEvaluationEvent addEvaluationEvent) {
		List<UserRole> userRoles = new ArrayList<>();
		for (String role : addEvaluationEvent.getAuthenticatedUser().getUserRole()) {
			userRoles.add(UserRole.fromString(role));
		}

		return userRoles;
	}

	private AuthenticatedUser getAuthenticatedUser(AddEvaluationEvent addEvaluationEvent) {
		return AuthenticatedUser.builder()
				.userName(addEvaluationEvent.getAuthenticatedUser().getUserName())
				.email(addEvaluationEvent.getEmail())
				.roles(this.getUserRoles(addEvaluationEvent))
				.build();
	}

	private String getResumeUrl(AddEvaluationEvent addEvaluationEvent, String documentId) throws Exception {
		String resumeUrl;
		try {
			resumeUrl = this.resumeUrlProcessor.getResumeUrl(
					addEvaluationEvent.getAuthenticatedUser().getUserName(),
					documentId,
					null,
					null);
		} catch (Exception exception) {
			log.error("Error while fetching resume url : ", exception);
			throw exception;
		}

		return resumeUrl;
	}
}
