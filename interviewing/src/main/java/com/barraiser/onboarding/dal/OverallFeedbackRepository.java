package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OverallFeedbackRepository extends JpaRepository<OverallFeedbackDAO, String> {
    OverallFeedbackDAO findByInterviewId(String interviewId);
}
