package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateAvailabilityRepository extends JpaRepository<CandidateAvailabilityDAO, String> {

    List<CandidateAvailabilityDAO> findAllByInterviewId(String interviewId);
}
