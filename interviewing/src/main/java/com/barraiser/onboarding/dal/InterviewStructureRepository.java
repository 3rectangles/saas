/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewStructureRepository extends JpaRepository<InterviewStructureDAO, String> {
	InterviewStructureDAO findByName(String name);

	Optional<InterviewStructureDAO> findByJiraIssueId(String id);

	Optional<InterviewStructureDAO> findById(String Id);

	List<InterviewStructureDAO> findAllByIdIn(List<String> ids);
}
