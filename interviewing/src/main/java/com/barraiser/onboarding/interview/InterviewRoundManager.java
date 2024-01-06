package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class InterviewRoundManager {
    private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;

    public Optional<JobRoleToInterviewStructureDAO> getJobRoleToInterviewStructureMapping(final String id) {
        return this.jobRoleToInterviewStructureRepository.findById(id);
    }

    public void add(final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
        this.jobRoleToInterviewStructureRepository.save(jobRoleToInterviewStructureDAO);
    }
}
