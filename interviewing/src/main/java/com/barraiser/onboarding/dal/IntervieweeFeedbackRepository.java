/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntervieweeFeedbackRepository extends JpaRepository<IntervieweeFeedbackDAO, String> {
	List<IntervieweeFeedbackDAO> findAllByInterviewIdIn(List<String> interviewIds);

	Page<IntervieweeFeedbackDAO> findAllByPartnerId(String partnerId, Pageable pageable);

	Optional<IntervieweeFeedbackDAO> findByInterviewId(String interviewId);
}
