package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewToEligibleExpertsRepository extends JpaRepository<InterviewToEligibleExpertsDAO, String> {

    List<InterviewToEligibleExpertsDAO> findAllByInterviewIdInAndInterviewerId(List<String> interviewIds, String interviewerId);

    List<InterviewToEligibleExpertsDAO> findAllByInterviewId(String id);
}
