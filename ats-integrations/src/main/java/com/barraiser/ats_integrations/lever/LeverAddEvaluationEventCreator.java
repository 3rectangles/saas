/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;
import com.barraiser.ats_integrations.events.ATSIntegrationsEventProducer;
import com.barraiser.common.graphql.types.Document;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.addevaluationevent.AddEvaluationEvent;
import com.barraiser.commons.eventing.schema.commons.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class LeverAddEvaluationEventCreator {
	private static final String SOURCE = "LEVER";
	private static final String PARTNER_USER_ROLE = "partner";

	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final LeverResumeDownloader leverResumeDownloader;
	private final ATSIntegrationsEventProducer eventProducer;

	public void createAddEvaluationEventForLever(
			final String partnerId,
			final LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData)
			throws Exception {
		log.info(String.format(
				"Adding lever opportunity for evaluation opportunityId %s applicationId %s partnerId %s",
				leverAddEvaluationEventCreationData.getOpportunityDTO().getId(),
				leverAddEvaluationEventCreationData.getLeverApplicationDTO().getId(),
				partnerId));

		List<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAOList = this.atsJobPostingToBRJobRoleRepository
				.findAllByAtsJobPostingIdAndAtsProvider(
						leverAddEvaluationEventCreationData
								.getLeverApplicationDTO()
								.getPosting(),
						ATSProvider.LEVER
								.getValue());

		for (ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO : atsJobPostingToBRJobRoleDAOList) {
			log.info(String.format(
					"Add evaluation of Lever opportunity:%s partnerId:%s for jobRoleId:%s",
					leverAddEvaluationEventCreationData.getOpportunityDTO().getId(),
					partnerId, atsJobPostingToBRJobRoleDAO.getBrJobRoleId()));

			final AddEvaluationEvent addEvaluationEvent = this.getAddEvaluationEvent(
					partnerId,
					leverAddEvaluationEventCreationData,
					atsJobPostingToBRJobRoleDAO);

			this.sendAddEvaluationEvent(addEvaluationEvent);

			this.saveLeverEvaluationDetailsToDatabase(
					addEvaluationEvent,
					leverAddEvaluationEventCreationData);

			log.info(String.format(
					"Evaluation added [evaluationId : %s] with JobRoleId : %s for lever opportunity opportunityId : %s for partnerId %s",
					addEvaluationEvent
							.getEvaluationId(),
					atsJobPostingToBRJobRoleDAO.getBrJobRoleId(),
					leverAddEvaluationEventCreationData
							.getOpportunityDTO()
							.getId(),
					partnerId));
		}
	}

	private void saveLeverEvaluationDetailsToDatabase(
			final AddEvaluationEvent addEvaluationEvent,
			final LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData) {
		final ATSToBREvaluationDAO atsToBREvaluationDAO = ATSToBREvaluationDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(addEvaluationEvent.getPartnerId())
				.brEvaluationId(addEvaluationEvent.getEvaluationId())
				.atsEvaluationId(
						leverAddEvaluationEventCreationData
								.getOpportunityDTO()
								.getId())
				.atsProvider(ATSProvider.LEVER.getValue())
				.build();

		this.atsToBREvaluationRepository.save(atsToBREvaluationDAO);
	}

	private AddEvaluationEvent getAddEvaluationEvent(
			final String partnerId,
			final LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData,
			final ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO)
			throws Exception {

		final AuthenticatedUser authenticatedUser = this
				.getAuthenticatedUserForEvent(leverAddEvaluationEventCreationData);

		final Document resume = this.leverResumeDownloader.getResumeFile(
				partnerId,
				leverAddEvaluationEventCreationData
						.getResumeDTOList()
						.get(0));

		return new AddEvaluationEvent()
				.evaluationId(
						UUID.randomUUID().toString())
				.candidateName(
						leverAddEvaluationEventCreationData
								.getOpportunityDTO()
								.getName())
				.documentLink(resume.getUrl())
				.jobRoleId(atsJobPostingToBRJobRoleDAO.getBrJobRoleId())
				.partnerId(partnerId)
				.phone(
						leverAddEvaluationEventCreationData
								.getOpportunityDTO()
								.getPhones()
								.get(0)
								.getValue())
				.email(
						leverAddEvaluationEventCreationData
								.getOpportunityDTO()
								.getEmails()
								.get(0))
				.pocEmail(authenticatedUser.getEmail())
				.source(SOURCE)
				.authenticatedUser(authenticatedUser);
	}

	private AuthenticatedUser getAuthenticatedUserForEvent(
			final LeverAddEvaluationEventCreationData leverAddEvaluationEventCreationData) {
		return new AuthenticatedUser()
				.userName(
						leverAddEvaluationEventCreationData
								.getUserDTO()
								.getName())
				.email(
						leverAddEvaluationEventCreationData
								.getUserDTO()
								.getEmail())
				.userRole(List.of(PARTNER_USER_ROLE));
	}

	private void sendAddEvaluationEvent(final AddEvaluationEvent addEvaluationEvent)
			throws Exception {
		final Event<AddEvaluationEvent> event = new Event<>();
		event.setPayload(addEvaluationEvent);

		this.eventProducer.pushEvent(event);
	}
}
