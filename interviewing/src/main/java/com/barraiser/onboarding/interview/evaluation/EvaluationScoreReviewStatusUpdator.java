/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.entitychange.EntityChange;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class EvaluationScoreReviewStatusUpdator implements EventListener<InterviewingConsumer> {

	private final ObjectMapper objectMapper;
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;

	private final String ENTITY_TYPE_INTERVIEW = "interview";

	@Override
	public List<Class> eventsToListen() {
		return List.of(EntityChange.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {

		final EntityChange entityChange = this.objectMapper.convertValue(event.getPayload(), EntityChange.class);

		if (this.ENTITY_TYPE_INTERVIEW.equalsIgnoreCase(entityChange.getEntityName())) {
			final String interviewId = this.getInterviewIdFromEvent(event);

			if (interviewId == null) {
				log.info("Invalid event. No interview ID present");
				return;
			}

			final EvaluationDAO evaluation = this.interviewUtil.getEvaluationForInterview(interviewId);
			this.updateEvaluationScoreReviewStatus(evaluation);
		}
	}

	private final String getInterviewIdFromEvent(final Event event) {
		return this.objectMapper.convertValue(event.getPayload(),
				EntityChange.class).getEntityId();
	}

	private void updateEvaluationScoreReviewStatus(final EvaluationDAO evaluation) {
		List<InterviewDAO> interviewDAOS = this.interViewRepository.findAllByEvaluationId(evaluation.getId());

		this.evaluationRepository.save(evaluation.toBuilder()
				.isEvaluationScoreUnderReview(this.doesEvaluationScoreNeedReview(interviewDAOS))
				.build());
	}

	private Boolean doesEvaluationScoreNeedReview(final List<InterviewDAO> interviews) {
		Boolean evaluationScoreNeedsReview = Boolean.FALSE;
		for (final InterviewDAO interviewDAO : interviews) {
			evaluationScoreNeedsReview = evaluationScoreNeedsReview
					|| this.doesInterviewScoreNeedReview(interviewDAO.getStatus());
		}
		return evaluationScoreNeedsReview;
	}

	private Boolean doesInterviewScoreNeedReview(final String interviewStatus) {
		return InterviewStatus.fromString(interviewStatus).isPendingQC()
				|| InterviewStatus.fromString(interviewStatus).isPendingCorrection();
	}
}
