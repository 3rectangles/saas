package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillWeightageRepository extends JpaRepository<SkillWeightageDAO, String> {

    List<SkillWeightageDAO> findAllByJobRoleIdAndJobRoleVersion(String jobRoleId, Integer jobRoleVersion);
}
