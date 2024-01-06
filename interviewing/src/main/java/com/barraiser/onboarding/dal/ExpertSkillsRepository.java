package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertSkillsRepository extends JpaRepository<ExpertSkillsDAO, String> {

    List<ExpertSkillsDAO> findAllBySkillIdIn(final List<String> skillIds);

    List<ExpertSkillsDAO> findAllByExpertIdIn(List<String> expertIds);

    List<ExpertSkillsDAO> findAllByExpertIdInAndSkillIdIn(List<String> expertIds, List<String> skillIds);

    List<ExpertSkillsDAO> findAllByExpertId(String id);

    Optional<ExpertSkillsDAO> findByExpertIdAndSkillId(String expertId, String skillId);
}
