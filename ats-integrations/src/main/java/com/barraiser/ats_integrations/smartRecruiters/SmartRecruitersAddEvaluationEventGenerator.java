/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.*;
import com.barraiser.ats_integrations.events.ATSIntegrationsEventProducer;
import com.barraiser.ats_integrations.smartRecruiters.DTO.UserDTO;
import com.barraiser.ats_integrations.smartRecruiters.POJO.SmartRecruitersAddEvaluationCreationData;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.addevaluationevent.AddEvaluationEvent;
import com.barraiser.commons.eventing.schema.commons.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersAddEvaluationEventGenerator {
	private static final String PARTNER_USER_ROLE = "partner";
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final ATSIntegrationsEventProducer eventProducer;
	private final ATSToBREvaluationRepository atsToBREvaluationRepository;

	public void generateAddEvaluationEvent(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final SmartRecruitersAddEvaluationCreationData addEvaluationCreationData) throws Exception {
		log.info(String.format(
				"Generating SR AddEvaluationEvent for atsEvaluationId:%s partnerId:%s atsProvider:%s",
				addEvaluationCreationData
						.getCandidateDTO()
						.getId(),
				partnerATSIntegrationDAO
						.getPartnerId(),
				partnerATSIntegrationDAO
						.getAtsProvider()));

		Optional<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAO = this.atsJobPostingToBRJobRoleRepository
				.findByAtsJobPostingIdAndAtsProvider(
						addEvaluationCreationData
								.getJobId(),
						partnerATSIntegrationDAO
								.getAtsProvider());

		if (atsJobPostingToBRJobRoleDAO.isEmpty()) {
			log.info("Connect BR job role not found");
			return;
		}

		final AddEvaluationEvent addEvaluationEvent = this.getAddEvaluationEvent(
				partnerATSIntegrationDAO,
				addEvaluationCreationData,
				atsJobPostingToBRJobRoleDAO.get());

		this.sendAddEvaluationEvent(addEvaluationEvent);

		this.saveSmartRecruitersEvaluationDetailsToDatabase(
				partnerATSIntegrationDAO,
				addEvaluationEvent,
				addEvaluationCreationData,
				atsJobPostingToBRJobRoleDAO.get());

		log.info(String.format(
				"Evaluation added evaluationId:%s JobRoleId:%s srCandidateID:%s partnerId:%s atsProvider:%s",
				addEvaluationEvent
						.getEvaluationId(),
				atsJobPostingToBRJobRoleDAO
						.get()
						.getBrJobRoleId(),
				addEvaluationCreationData
						.getCandidateDTO()
						.getId(),
				partnerATSIntegrationDAO
						.getPartnerId(),
				partnerATSIntegrationDAO
						.getAtsProvider()));
	}

	private void saveSmartRecruitersEvaluationDetailsToDatabase(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final AddEvaluationEvent addEvaluationEvent,
			final SmartRecruitersAddEvaluationCreationData addEvaluationCreationData,
			final ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO) {
		final ATSToBREvaluationDAO atsToBREvaluationDAO = ATSToBREvaluationDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(partnerATSIntegrationDAO.getPartnerId())
				.atsProvider(partnerATSIntegrationDAO.getAtsProvider())
				.atsEvaluationId(addEvaluationCreationData
						.getCandidateDTO()
						.getId())
				.brEvaluationId(addEvaluationEvent.getEvaluationId())
				.atsJobPostingToBRJobRoleId(atsJobPostingToBRJobRoleDAO.getId())
				.build();

		this.atsToBREvaluationRepository
				.save(atsToBREvaluationDAO);
	}

	private AddEvaluationEvent getAddEvaluationEvent(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final SmartRecruitersAddEvaluationCreationData addEvaluationCreationData,
			final ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO) {
		final AuthenticatedUser authenticatedUser = this
				.getAuthenticatedUser(addEvaluationCreationData.getHiringManager());

		return new AddEvaluationEvent()
				.evaluationId(UUID.randomUUID().toString())
				.candidateName(String.format(
						"%s %s",
						addEvaluationCreationData
								.getCandidateDTO()
								.getFirstName(),
						addEvaluationCreationData
								.getCandidateDTO()
								.getLastName()))
				.documentLink(addEvaluationCreationData
						.getResume()
						.getUrl())
				.jobRoleId(atsJobPostingToBRJobRoleDAO.getBrJobRoleId())
				.phone(addEvaluationCreationData
						.getCandidateDTO()
						.getPhoneNumber())
				.email(addEvaluationCreationData
						.getCandidateDTO()
						.getEmail())
				.pocEmail(authenticatedUser.getEmail())
				.partnerId(partnerATSIntegrationDAO.getPartnerId())
				.source(partnerATSIntegrationDAO.getAtsProvider())
				.authenticatedUser(authenticatedUser);
	}

	private AuthenticatedUser getAuthenticatedUser(final UserDTO userDTO) {
		return new AuthenticatedUser()
				.userName(String.format(
						"%s %s",
						userDTO.getFirstName(),
						userDTO.getLastName()))
				.email(userDTO.getEmail())
				.userRole(List.of(PARTNER_USER_ROLE));
	}

	private void sendAddEvaluationEvent(final AddEvaluationEvent addEvaluationEvent)
			throws Exception {
		final Event<AddEvaluationEvent> event = new Event<>();

		event.setPayload(addEvaluationEvent);

		this.eventProducer.pushEvent(event);
	}
}
