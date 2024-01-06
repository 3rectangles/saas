package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewStructureSkillsDAO;
import com.barraiser.onboarding.dal.InterviewStructureSkillsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InterviewStructureSkillManager {
    private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;

    public void add(final List<InterviewStructureSkillsDAO> interviewStructureSkillsDAOs) {
        this.interviewStructureSkillsRepository.saveAll(interviewStructureSkillsDAOs);
    }

    public void add(final InterviewStructureSkillsDAO interviewStructureSkillsDAO) {
        this.interviewStructureSkillsRepository.save(interviewStructureSkillsDAO);
    }

    public void delete(final String interviewStructureId) {
        this.interviewStructureSkillsRepository.deleteByInterviewStructureId(interviewStructureId);
    }
}
