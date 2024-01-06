/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JiraUUIDRepository extends JpaRepository<JiraUUIDDAO, String> {
	Optional<JiraUUIDDAO> findByJira(String jira);

	Optional<JiraUUIDDAO> findByUuid(String uuid);

	void deleteByUuid(String uuid);

	List<JiraUUIDDAO> findAllByUuid(String id);
}
