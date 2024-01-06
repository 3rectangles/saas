/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.specifications.InterviewHistorySpecifications;

import lombok.AllArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ExpertInterviewsFetcher {
	private final InterviewHistoryManager interviewHistoryManager;
	private final InterviewHistorySpecifications interviewHistorySpecifications;

	/**
	 * @param partnerId
	 *            will be passed incase of Saas but not incase of Iaas
	 */
	public List<InterviewDAO> getInterviewsForExpert(
			final String partnerId,
			final String interviewerId,
			final Long startDate,
			final Long endDate,
			final List<String> includedStatus,
			final List<String> excludedStatus) {
		final List<InterviewHistoryDAO> interviewHistoryDAOs = this.interviewHistoryManager
				.getLatestInterviewChangeHistoriesByInterviewerIdAndField(
						partnerId, interviewerId, "rescheduleCount");
		List<InterviewHistoryDAO> filteredInterviewHistories = this.filterByIncludedStatus(interviewHistoryDAOs,
				includedStatus);
		filteredInterviewHistories = this.filterByExcludedStatus(filteredInterviewHistories, excludedStatus);
		filteredInterviewHistories = this.filterByStartDate(filteredInterviewHistories, startDate);
		filteredInterviewHistories = this.filterByEndDate(filteredInterviewHistories,
				endDate);
		filteredInterviewHistories = this.filterDuplicateInterviews(filteredInterviewHistories);
		return this.interviewHistoryManager.mapInterviewHistoriesToInterviews(
				filteredInterviewHistories);
	}

	private List<InterviewHistoryDAO> filterByIncludedStatus(
			final List<InterviewHistoryDAO> interviewHistoryDAOs,
			final List<String> includedStatus) {
		if (includedStatus == null) {
			return interviewHistoryDAOs;
		}
		return interviewHistoryDAOs.stream()
				.filter(x -> includedStatus.contains(x.getStatus()))
				.collect(Collectors.toList());
	}

	private List<InterviewHistoryDAO> filterByExcludedStatus(
			final List<InterviewHistoryDAO> interviewHistoryDAOs,
			final List<String> excludedStatus) {
		if (excludedStatus == null) {
			return interviewHistoryDAOs;
		}
		return interviewHistoryDAOs.stream()
				.filter(x -> !excludedStatus.contains(x.getStatus()))
				.collect(Collectors.toList());
	}

	private List<InterviewHistoryDAO> filterByStartDate(
			final List<InterviewHistoryDAO> interviewHistoryDAOs, final Long startDate) {
		if (startDate == null) {
			return interviewHistoryDAOs;
		}
		return interviewHistoryDAOs.stream()
				.filter(x -> x.getStartDate() != null && x.getStartDate() >= startDate)
				.collect(Collectors.toList());
	}

	private List<InterviewHistoryDAO> filterByEndDate(
			final List<InterviewHistoryDAO> interviewHistoryDAOs, final Long endDate) {
		if (endDate == null) {
			return interviewHistoryDAOs;
		}
		return interviewHistoryDAOs.stream()
				.filter(x -> x.getStartDate() != null && x.getStartDate() < endDate)
				.collect(Collectors.toList());
	}

	private List<InterviewHistoryDAO> filterDuplicateInterviews(
			final List<InterviewHistoryDAO> interviewHistoryDAOs) {
		return interviewHistoryDAOs.stream()
				.filter(x -> x.getDuplicateReason() == null)
				.collect(Collectors.toList());
	}

	public List<InterviewDAO> getInterviewsForExpertUsingSpecification(
			final String interviewerId,
			final Long startDate,
			final Long endDate,
			final List<String> includedStatus,
			final List<String> excludedStatus) {
		final Specification<InterviewHistoryDAO> specifications = this.interviewHistorySpecifications
				.getExpertsInterviewsSpecification(
						interviewerId, startDate, endDate, includedStatus, excludedStatus);
		final List<InterviewHistoryDAO> interviewHistoryDAOs = this.interviewHistoryManager
				.getLatestInterviewChangeHistoriesByInterviewerIdAndField(
						specifications, "rescheduleCount");
		return this.interviewHistoryManager.mapInterviewHistoriesToInterviews(interviewHistoryDAOs);
	}
}
