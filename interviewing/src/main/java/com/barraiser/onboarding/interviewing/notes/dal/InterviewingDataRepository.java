/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.notes.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewingDataRepository extends JpaRepository<InterviewingDataDAO, String> {

	Optional<InterviewingDataDAO> findByInterviewId(String interviewId);
}
