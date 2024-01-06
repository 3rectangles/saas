/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ExpertRepository extends JpaRepository<ExpertDAO, String> {

	List<ExpertDAO> findAllByDuplicatedFrom(String expertId);

	List<ExpertDAO> findAllByDuplicatedFromIn(List<String> userIds);

	List<ExpertDAO> findAllByIdInAndDuplicatedFromIsNotNull(List<String> userIds);

	List<ExpertDAO> findAllByIdIn(Set<String> interviewerIds);

	List<ExpertDAO> findAllByIdIn(List<String> interviewerIds);

	List<ExpertDAO> findAllByTenantId(String tenantId);
}
