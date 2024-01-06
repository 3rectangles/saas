/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewCostDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface InterviewCostRepository extends JpaRepository<InterviewCostDAO, String> {

	List<InterviewCostDAO> findAllByInterviewIdIn(List<String> interviewIds);

	InterviewCostDAO findByInterviewIdAndRescheduleCountAndInterviewerId(String interviewId, Integer rescheduleCount,
			String interviewerId);

	List<InterviewCostDAO> findAllByInterviewerIdIn(Set<String> interviewerIds);
}
