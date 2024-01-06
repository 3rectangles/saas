/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class LeverCandidateAdditionHandler {
	private static final String BARRAISER_EVALUATION_STARTED_TAG = "BarRaiser Evaluation Started";
	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final LeverNotesHandler leverNotesHandler;
	private final LeverTagsHandler leverTagsHandler;

	public void sendPartnerPortalLinkToLever(
			final CandidateAddition candidateAddition) throws Exception {
		log.info("Sending partner portal link to lever after receiving CandidateAddition event");

		Optional<ATSToBREvaluationDAO> leverDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationIdAndAtsProvider(
						candidateAddition
								.getCandidate()
								.getEvaluationId(),
						ATSProvider.LEVER
								.getValue());

		if (leverDAO.isEmpty()) {
			return;
		}

		log.info(String.format(
				"Sending partner portal link evaluationId:%s lever_opportunityId:%s partnerId:%s",
				leverDAO.get().getBrEvaluationId(),
				leverDAO.get().getAtsEvaluationId(),
				leverDAO.get().getPartnerId()));

		final String partnerPortalLink = this.getPartnerPortalLink(leverDAO.get());

		final String note = this.getNoteForLever(partnerPortalLink);

		this.leverNotesHandler.addNoteToLeverOpportunity(
				leverDAO
						.get()
						.getPartnerId(),
				leverDAO
						.get()
						.getAtsEvaluationId(),
				note);
	}

	private String getNoteForLever(final String partnerPortalLink) {
		return String.format(
				"BarRaiser Evaluation Started.\nView BarRaiser Evaluation Process - %s",
				partnerPortalLink);
	}

	public void addBarRaiserEvaluationStartedTagToLever(final CandidateAddition candidateAddition) throws Exception {
		log.info("Sending BarRaiserEvaluationStarted to lever after receiving CandidateAddition event");

		Optional<ATSToBREvaluationDAO> leverDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationIdAndAtsProvider(
						candidateAddition
								.getCandidate()
								.getEvaluationId(),
						ATSProvider.LEVER
								.getValue());

		if (leverDAO.isEmpty()) {
			return;
		}

		log.info(String.format(
				"Adding %s tag to lever opportunity evaluationId:%s lever_opportunityId:%s partnerId:%s",
				BARRAISER_EVALUATION_STARTED_TAG,
				leverDAO.get().getBrEvaluationId(),
				leverDAO.get().getAtsEvaluationId(),
				leverDAO.get().getPartnerId()));

		final List<String> tagsToAddToLeverOpportunity = new ArrayList<>();

		tagsToAddToLeverOpportunity
				.add(BARRAISER_EVALUATION_STARTED_TAG);

		this.leverTagsHandler.addTagsToLeverOpportunity(
				leverDAO
						.get()
						.getPartnerId(),
				leverDAO
						.get()
						.getAtsEvaluationId(),
				tagsToAddToLeverOpportunity);
	}

	private String getPartnerPortalLink(final ATSToBREvaluationDAO leverDAO) {
		return String.format(
				"https://app.barraiser.com/partner/%s/evaluations?eid=%s",
				leverDAO.getPartnerId(),
				leverDAO.getBrEvaluationId());
	}
}
