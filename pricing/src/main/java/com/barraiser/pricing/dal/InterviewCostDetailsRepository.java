/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewCostDetailsRepository extends JpaRepository<InterviewCostDetailsDAO, String> {
	Optional<InterviewCostDetailsDAO> findByInterviewIdAndRescheduleCountAndExpertId(String interviewId,
			Integer rescheduleCount, String expertId);

	Optional<InterviewCostDetailsDAO> findTopByInterviewIdAndRescheduleCountOrderByRescheduleCountDescCreatedOnDesc(
			String interviewId,
			Integer rescheduleCount);

	Optional<InterviewCostDetailsDAO> findTopByInterviewIdOrderByRescheduleCountDescCreatedOnDesc(String interviewId);
}
