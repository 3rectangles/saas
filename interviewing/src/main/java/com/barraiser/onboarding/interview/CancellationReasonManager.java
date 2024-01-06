/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.CancellationReasonDAO;
import com.barraiser.onboarding.dal.CancellationReasonRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.CANCELLATION_TYPE_CANDIDATE_AND_EXPERT;

@Component
@AllArgsConstructor
public class CancellationReasonManager {
	private final CancellationReasonRepository cancellationReasonRepository;
	public static final String CANCELLATION_TYPE_EXPERT = "EXPERT";
	public static final String CANCELLATION_TYPE_CANDIDATE = "CANDIDATE";
	private static final String CANCELLATION_REASON_CANDIDATE_AND_EXPERT_DID_NOT_JOIN = "CIRCUMSTANTIAL";
	private static final String EXPERT_CANCELLATION_REASON = "Expert did not join the interview";

	public List<CancellationReasonDAO> getCancellationReasonsForInterviewsCancelledByExpert() {
		return this.cancellationReasonRepository
				.findAllByCancellationTypeIn(List.of(CANCELLATION_TYPE_EXPERT, CANCELLATION_TYPE_CANDIDATE_AND_EXPERT));
	}

	public List<String> getCancellationReasonsByUserTypeAndProcessType(final List<String> userType,
			final String processType) {
		return this.cancellationReasonRepository
				.findByCancellationTypeInAndIsActiveTrueAndProcessType(userType, processType).get()
				.stream()
				.map(CancellationReasonDAO::getId)
				.collect(Collectors.toList());
	}

	public Map<String, Integer> getCountPerCancellationReason(final List<InterviewDAO> interviews,
			final List<CancellationReasonDAO> cancellationReasonDAOs) {
		final List<String> cancellationReasonIds = cancellationReasonDAOs.stream().map(CancellationReasonDAO::getId)
				.collect(Collectors.toList());
		final Map<String, Integer> countPerCancellationReason = new HashMap<>();
		for (InterviewDAO interviewDAO : interviews) {
			if (InterviewStatus.CANCELLATION_DONE.getValue().equals(interviewDAO.getStatus()) && cancellationReasonIds
					.contains(interviewDAO.getCancellationReasonId())) {
				String cancellationReason = cancellationReasonDAOs.stream()
						.filter(x -> x.getId().equals(interviewDAO
								.getCancellationReasonId()))
						.findFirst().get().getCancellationReason();
				if (Objects.equals(cancellationReason, CANCELLATION_REASON_CANDIDATE_AND_EXPERT_DID_NOT_JOIN))
					cancellationReason = EXPERT_CANCELLATION_REASON;
				countPerCancellationReason.put(cancellationReason,
						countPerCancellationReason.getOrDefault(cancellationReason, 0) + 1);
			}
		}
		return countPerCancellationReason;
	}

	public Boolean isCancelledByExpert(final String cancellationReasonId) {
		return cancellationReasonId != null && (CANCELLATION_TYPE_EXPERT
				.equals(this.cancellationReasonRepository.findById(cancellationReasonId).get().getCancellationType()) ||
				CANCELLATION_TYPE_CANDIDATE_AND_EXPERT.equals(
						this.cancellationReasonRepository.findById(cancellationReasonId).get().getCancellationType()));
	}

	public String getDisplayReason(final String cancellationReasonId) {
		return this.cancellationReasonRepository.findById(cancellationReasonId).get().getCustomerDisplayableReason();
	}

	public Boolean isCancelledByCandidate(final CancellationReasonDAO cancellationReasonDAO) {
		return CANCELLATION_TYPE_CANDIDATE.equals(cancellationReasonDAO.getCancellationType());
	}

	public Boolean isCancelledByCandidateAndExpert(final CancellationReasonDAO cancellationReasonDAO) {
		return CANCELLATION_TYPE_CANDIDATE_AND_EXPERT.equals(cancellationReasonDAO.getCancellationType());
	}

	public CancellationReasonDAO getCancellationReasonForId(final String cancellationReasonId) {
		return this.cancellationReasonRepository.findById(cancellationReasonId).get();
	}
}
