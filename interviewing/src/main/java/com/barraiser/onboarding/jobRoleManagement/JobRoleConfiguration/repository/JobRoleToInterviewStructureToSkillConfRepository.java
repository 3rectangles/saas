/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.repository;

import com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.dal.JobRoleToInterviewStructureToSkillConfDAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRoleToInterviewStructureToSkillConfRepository
		extends JpaRepository<JobRoleToInterviewStructureToSkillConfDAO, String>,
		JpaSpecificationExecutor<JobRoleToInterviewStructureToSkillConfDAO> {
}
