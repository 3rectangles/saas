/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class LeverEvaluationCompletedHandler {
	private static final String BARRAISER_EVALUATION_COMPLETED_TAG = "BarRaiser Evaluation Completed";

	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final LeverNotesHandler leverNotesHandler;
	private final LeverTagsHandler leverTagsHandler;

	public void sendCandidateEvaluationLinkToLever(
			final EvaluationCompleted evaluationCompleted)
			throws Exception {
		log.info("Lever candidate evaluation link sender after receiving EvaluationCompleted event");

		Optional<ATSToBREvaluationDAO> leverDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationIdAndAtsProvider(
						evaluationCompleted
								.getEvaluation()
								.getId(),
						ATSProvider.LEVER
								.getValue());

		if (leverDAO.isEmpty()) {
			return;
		}

		log.info(String.format(
				"Sending candidate evaluation link for evaluationId %s for lever opportunityId %s for partnerId %s",
				leverDAO.get().getBrEvaluationId(),
				leverDAO.get().getAtsEvaluationId(),
				leverDAO.get().getPartnerId()));

		final String candidateEvaluationLink = this.getCandidateEvaluationLink(
				leverDAO
						.get()
						.getBrEvaluationId());

		final String note = this.getNoteForLever(candidateEvaluationLink);

		this.leverNotesHandler.addNoteToLeverOpportunity(
				leverDAO
						.get()
						.getPartnerId(),
				leverDAO
						.get()
						.getAtsEvaluationId(),
				note);
	}

	private String getNoteForLever(final String candidateEvaluationLink) {
		return String.format("BarRaiser Evaluation Completed.\nView BarRaiser report - %s", candidateEvaluationLink);
	}

	public void addBarRaiserEvaluationCompletedTagToLever(
			final EvaluationCompleted evaluationCompleted)
			throws Exception {
		log.info("Sending BarRaiserEvaluationStarted to lever after receiving CandidateAddition event");

		Optional<ATSToBREvaluationDAO> leverDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationIdAndAtsProvider(
						evaluationCompleted
								.getEvaluation()
								.getId(),
						ATSProvider.LEVER
								.getValue());

		if (leverDAO.isEmpty()) {
			return;
		}

		log.info(String.format(
				"Adding %s tag to lever opportunity evaluationId:%s lever_opportunityId:%s partnerId:%s",
				BARRAISER_EVALUATION_COMPLETED_TAG,
				leverDAO.get().getBrEvaluationId(),
				leverDAO.get().getAtsEvaluationId(),
				leverDAO.get().getPartnerId()));

		final List<String> tagsToAddToLeverOpportunity = new ArrayList<>();

		tagsToAddToLeverOpportunity
				.add(BARRAISER_EVALUATION_COMPLETED_TAG);

		this.leverTagsHandler.addTagsToLeverOpportunity(
				leverDAO
						.get()
						.getPartnerId(),
				leverDAO
						.get()
						.getAtsEvaluationId(),
				tagsToAddToLeverOpportunity);
	}

	private String getCandidateEvaluationLink(final String evaluationId) {
		return String.format(
				"https://app.barraiser.com/candidate-evaluation/%s",
				evaluationId);
	}
}
