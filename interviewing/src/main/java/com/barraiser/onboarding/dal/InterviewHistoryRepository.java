/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewHistoryRepository
		extends JpaRepository<InterviewHistoryDAO, String>, JpaSpecificationExecutor<InterviewHistoryDAO> {
	List<InterviewHistoryDAO> findAllByInterviewIdInAndCreatedOnIsNotNullOrderByCreatedOnAsc(List<String> interviewIds);

	InterviewHistoryDAO findTopByInterviewIdAndStatusAndCreatedOnIsNotNullOrderByCreatedOnDesc(String interviewId,
			String status);

	List<InterviewHistoryDAO> findByInterviewIdAndCreatedOnIsNotNullOrderByCreatedOnAsc(String interviewId);

	List<InterviewHistoryDAO> findAllByInterviewerId(String interviewerId);

	InterviewHistoryDAO findTopByInterviewIdAndRescheduleCountAndStatusAndCreatedOnIsNotNullOrderByCreatedOnDesc(
			String interviewId, Integer rescheduleCount, String status);

	InterviewHistoryDAO findTopByInterviewIdAndRescheduleCountAndCreatedOnIsNotNullOrderByCreatedOnDesc(
			String interviewId, Integer rescheduleCount);

	InterviewHistoryDAO findTopByInterviewIdAndRescheduleCountAndInterviewerIdOrderByCreatedOnDesc(String interviewId,
			Integer rescheduleCount, String interviewerId);

	List<InterviewHistoryDAO> findAllByPartnerId(String partnerId);

	List<InterviewHistoryDAO> findAllByInterviewerIdAndPartnerId(String interviewerId, String partnerId);
}
