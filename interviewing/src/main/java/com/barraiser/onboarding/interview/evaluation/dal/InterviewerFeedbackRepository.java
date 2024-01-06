/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewerFeedbackRepository extends JpaRepository<InterviewerFeedbackDAO, String> {

	List<InterviewerFeedbackDAO> findByInterviewId(final String interviewId);

}
