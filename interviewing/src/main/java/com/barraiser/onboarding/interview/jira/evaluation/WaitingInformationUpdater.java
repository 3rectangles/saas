/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.onboarding.dal.ReasonDAO;
import com.barraiser.onboarding.dal.WaitingInformationDAO;
import com.barraiser.onboarding.dal.WaitingInformationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@Component
@Log4j2
public class WaitingInformationUpdater {
	public static final String BARRAISER = "BARRAISER";
	private final WaitingInformationRepository waitingInformationRepository;

	public void update(final ReasonDAO reasonDAO, final String evaluationId) {
		if (reasonDAO != null) {
			Optional<WaitingInformationDAO> prevWaitingClientReasonDAO = this.waitingInformationRepository
					.findById(evaluationId);
			if (prevWaitingClientReasonDAO.isPresent()) {
				this.waitingInformationRepository.save(
						prevWaitingClientReasonDAO.get().toBuilder()
								.reason(reasonDAO.getReason())
								.waitingReasonId(reasonDAO.getId())
								.updatedBy(BARRAISER)
								.updatedOn(Instant.now())
								.build());

			} else {
				this.waitingInformationRepository.save(
						WaitingInformationDAO.builder()
								.evaluationId(evaluationId)
								.reason(reasonDAO.getReason())
								.waitingReasonId(reasonDAO.getId())
								.updatedBy(BARRAISER)
								.updatedOn(Instant.now())
								.build());
			}
		} else {
			log.error("Waiting Reason is not set");
		}
	}
}
