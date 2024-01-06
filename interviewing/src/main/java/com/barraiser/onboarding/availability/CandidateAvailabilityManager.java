package com.barraiser.onboarding.availability;

import com.barraiser.onboarding.dal.CandidateAvailabilityDAO;
import com.barraiser.onboarding.dal.CandidateAvailabilityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class CandidateAvailabilityManager {
    private final CandidateAvailabilityRepository candidateAvailabilityRepository;

    @Transactional
    public void updateSlots(final String userId, final String interviewId, final List<AvailabilitySlot> slots) {
        this.deletePreviousAvailabilityGivenByCandidate(interviewId);
        final List<CandidateAvailabilityDAO> candidateAvailabilityDAOs = slots.stream()
            .map(x -> CandidateAvailabilityDAO.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .interviewId(interviewId)
            .startDate(x.getStartDate())
            .endDate(x.getEndDate())
            .build()).collect(Collectors.toList());
        this.candidateAvailabilityRepository.saveAll(candidateAvailabilityDAOs);
    }

    public List<CandidateAvailabilityDAO> getCandidateAvailabilitySlots(final String interviewId) {
        return this.candidateAvailabilityRepository.findAllByInterviewId(interviewId);
    }

    public void deletePreviousAvailabilityGivenByCandidate(final String interviewId) {
        List<CandidateAvailabilityDAO> candidateAvailabilityDAOs = this.candidateAvailabilityRepository.findAllByInterviewId(interviewId);
        candidateAvailabilityDAOs = candidateAvailabilityDAOs.stream().map(
            x -> x.toBuilder().deletedOn(Instant.now().getEpochSecond()).build()).collect(Collectors.toList());
        this.candidateAvailabilityRepository.saveAll(candidateAvailabilityDAOs);
    }
}
