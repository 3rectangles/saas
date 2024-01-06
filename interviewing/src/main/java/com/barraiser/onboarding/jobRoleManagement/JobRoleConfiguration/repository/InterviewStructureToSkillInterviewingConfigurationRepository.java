/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.repository;

import com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.dal.InterviewStructureToSkillInterviewingConfigurationDAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewStructureToSkillInterviewingConfigurationRepository
		extends JpaRepository<InterviewStructureToSkillInterviewingConfigurationDAO, String> {
	List<InterviewStructureToSkillInterviewingConfigurationDAO> findAllByInterviewStructureId(
			String interviewStructureId);
}
