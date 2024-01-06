/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.repository;

import com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.dal.EntityToDocumentMappingDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityToDocumentMappingRepository extends JpaRepository<EntityToDocumentMappingDAO, String> {

	List<EntityToDocumentMappingDAO> findByEntityIdAndEntityVersionAndEntityTypeAndContext(final String entityId,
			final Integer entityVersion, final String entityType, final String context);

}
