package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewProcessQualityRepository extends JpaRepository<InterviewProcessQualityDAO,String> {
    Optional<InterviewProcessQualityDAO> findByInterviewId(String interviewId);
}
