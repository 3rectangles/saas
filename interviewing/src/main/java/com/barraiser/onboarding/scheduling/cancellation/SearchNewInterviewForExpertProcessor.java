/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertDeAllocationProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
// TODO: remove implements
public class SearchNewInterviewForExpertProcessor implements ExpertDeAllocationProcessor {
	// CancellationProcessor is for backward compatibility and needs to be removed
	private final ExpertRepository expertRepository;
	private final InterviewToEligibleExpertsRepository interviewToEligibleExpertsRepository;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;

	private InterviewDAO getInterviewThatExpertCanTake(final Long startDate, final Long endDate,
			final String interviewerId) {
		List<InterviewDAO> interviewDAOs = this.getAllInterviewsScheduledWithInThatSlot(startDate, endDate);
		final List<ExpertDAO> duplicateExperts = this.getAllDuplicateExpertsForInterviews(interviewDAOs);
		final List<String> expertIds = duplicateExperts.stream().map(ExpertDAO::getId).collect(Collectors.toList());
		interviewDAOs = interviewDAOs.stream().filter(x -> expertIds.contains(x.getInterviewerId()))
				.collect(Collectors.toList());
		final List<InterviewDAO> interviewsThatExpertCanTake = this.getInterviewsThatExpertCanTake(interviewDAOs,
				interviewerId);
		final List<InterviewDAO> sortedInterviews = this.sortInterviews(interviewsThatExpertCanTake, duplicateExperts,
				interviewerId);
		return sortedInterviews.size() > 0 ? sortedInterviews.get(0) : null;
	}

	private List<InterviewDAO> getAllInterviewsScheduledWithInThatSlot(final Long startDate, final Long endDate) {
		final List<InterviewDAO> interviewDAOs = this.interViewRepository
				.findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);
		return interviewDAOs.stream().filter(x -> !InterviewStatus.CANCELLATION_DONE.getValue().equals(x.getStatus()) &&
				!InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue().equals(x.getStatus()))
				.collect(Collectors.toList());
	}

	private List<ExpertDAO> getAllDuplicateExpertsForInterviews(final List<InterviewDAO> interviews) {
		final List<ExpertDAO> duplicateExperts = this.expertRepository.findAllByIdInAndDuplicatedFromIsNotNull(
				interviews.stream().map(InterviewDAO::getInterviewerId).collect(Collectors.toList()));
		return duplicateExperts;
	}

	private List<InterviewDAO> sortInterviews(final List<InterviewDAO> interviews,
			final List<ExpertDAO> duplicateExperts, final String originalInterviewerId) {
		final List<InterviewDAO> sortedInterviews = new ArrayList<>();
		for (final InterviewDAO interview : interviews) {
			final String duplicateExpertId = interview.getInterviewerId();
			final Optional<ExpertDAO> duplicateExpert = duplicateExperts.stream()
					.filter(x -> x.getId().equals(duplicateExpertId)).findFirst();
			if (duplicateExpert.isPresent()
					&& duplicateExpert.get().getDuplicatedFrom().equals(originalInterviewerId)) {
				sortedInterviews.add(0, interview);
			} else {
				sortedInterviews.add(interview);
			}
		}
		return sortedInterviews;
	}

	private List<InterviewDAO> getInterviewsThatExpertCanTake(List<InterviewDAO> interviews,
			final String interviewerId) {
		final List<String> interviewIds = interviews.stream().map(InterviewDAO::getId).collect(Collectors.toList());
		final List<InterviewToEligibleExpertsDAO> interviewToEligibleExpertsDAOs = this.interviewToEligibleExpertsRepository
				.findAllByInterviewIdInAndInterviewerId(interviewIds, interviewerId);
		final List<String> interviewIdsThatExpertCanTake = interviewToEligibleExpertsDAOs.stream()
				.map(InterviewToEligibleExpertsDAO::getInterviewId).distinct().collect(Collectors.toList());
		interviews = interviews.stream().filter(x -> interviewIdsThatExpertCanTake.contains(x.getId()))
				.collect(Collectors.toList());
		return this.filterInterviewsForWhichInterviewerCannotBeAssigned(interviews, interviewerId);
	}

	private List<InterviewDAO> filterInterviewsForWhichInterviewerCannotBeAssigned(final List<InterviewDAO> interviews,
			final String interviewerId) {
		final List<EvaluationDAO> evaluationDAOs = this.evaluationRepository.findAllByIdIn(interviews.stream().map(
				InterviewDAO::getEvaluationId).collect(Collectors.toList()));

		final List<String> evaluationIdsForWhichInterviewerHasAlreadyBeenUsed = this.interViewRepository
				.findAllByEvaluationIdInAndInterviewerIdAndStatusNotIn(
						evaluationDAOs.stream().map(EvaluationDAO::getId).collect(Collectors.toList()), interviewerId,
						List.of(InterviewStatus.CANCELLATION_DONE.getValue()))
				.stream().map(InterviewDAO::getEvaluationId)
				.collect(Collectors.toList());

		return interviews.stream().filter(x -> !evaluationIdsForWhichInterviewerHasAlreadyBeenUsed
				.contains(x.getEvaluationId())).collect(Collectors.toList());
	}

	@Override
	public void process(final ExpertDeAllocatorData data) throws Exception {
		final InterviewDAO cancelledInterview = data.getInterview();
		final InterviewDAO interviewDAO = this.getInterviewThatExpertCanTake(
				cancelledInterview.getStartDate(),
				cancelledInterview.getEndDate(), data.getOriginalInterviewerId());
		data.setNewInterviewThatExpertCanTake(interviewDAO);
	}
}
