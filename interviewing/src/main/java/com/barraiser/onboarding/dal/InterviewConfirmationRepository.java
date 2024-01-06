package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewConfirmationRepository extends JpaRepository<InterviewConfirmationDAO, String> {

    Optional<InterviewConfirmationDAO> findTopByInterviewIdAndCandidateConfirmationTimeNotNullAndRescheduleCountOrderByCandidateConfirmationTimeDesc(
        String interviewId, Integer rescheduleCount);

    Optional<InterviewConfirmationDAO> findByInterviewIdAndCommunicationChannelAndRescheduleCount(String interviewId, String channel, Integer rescheduleCount);

    Optional<InterviewConfirmationDAO> findTopByInterviewIdAndRescheduleCountOrderByCandidateConfirmationTimeDesc(String id, Integer rescheduleCount);
}
