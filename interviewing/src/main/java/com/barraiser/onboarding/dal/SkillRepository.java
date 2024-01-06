/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<SkillDAO, String> {
	List<SkillDAO> findAllByIdInAndDomainIsNull(List<String> skillsToFocus);

	void deleteByIdIn(List<String> skillIds);

	Optional<SkillDAO> findByNameAndDomainIsNull(String skillName);

	List<SkillDAO> findAllByIdIn(List<String> skillIds);

	List<SkillDAO> findAllByDomainAndDeprecatedOnIsNull(String domainId);

	List<SkillDAO> findAllByDomainIsNullAndDeprecatedOnIsNull();

	List<SkillDAO> findAllByDomainInAndDeprecatedOnIsNull(List<String> domainIds);

	Optional<SkillDAO> findByNameAndDomainAndParent(String name, String domainId, String parentSkillId);

	Optional<SkillDAO> findByNameIgnoreCaseAndDomainAndParent(String name, String domainId, String parentSkillId);
}
