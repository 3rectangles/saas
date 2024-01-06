/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRoleToInterviewStructureRepository
		extends JpaRepository<JobRoleToInterviewStructureDAO, String> {

	Optional<JobRoleToInterviewStructureDAO> findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
			String jobRoleId, Integer jobRoleVersion, String interviewStructureId);

	@Query(value = "SELECT * FROM job_role_to_interview_structure WHERE concat(job_role_id,"
			+ " job_role_version) IN :ids", nativeQuery = true)
	List<JobRoleToInterviewStructureDAO> findAllByJobRoleIdJobRoleVersionIn(List<String> ids);

	List<JobRoleToInterviewStructureDAO> findAllByJobRoleIdAndJobRoleVersion(
			String jobRoleId, Integer jobRoleVersion);

	long countByJobRoleIdAndJobRoleVersionAndInterviewRoundNotIn(
			String jobRoleId, Integer jobRoleVersion, List<String> interviewRound);

	List<JobRoleToInterviewStructureDAO> findAllByJobRoleIdAndJobRoleVersionOrderByOrderIndexAsc(
			String jobRoleId, Integer jobRoleVersion);

	List<JobRoleToInterviewStructureDAO> findByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc(
			String jobRoleId, Integer jobRoleVersion, Integer orderIndex);

	Optional<JobRoleToInterviewStructureDAO> findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc(
			String jobRoleId, Integer jobRoleVersion, Integer orderIndex);

	Optional<JobRoleToInterviewStructureDAO> findByJobRoleIdAndJobRoleVersionAndOrderIndex(
			String jobRoleId, Integer jobRoleVersion, Integer orderIndex);

	JobRoleToInterviewStructureDAO findTopByJobRoleIdAndJobRoleVersionOrderByOrderIndexDesc(String jobRoleId,
			Integer jobRoleVersion);

	JobRoleToInterviewStructureDAO findByInterviewStructureId(String interviewStructureId);

	List<JobRoleToInterviewStructureDAO> findTopByJobRoleIdOrderByJobRoleVersionDescOrderIndexAsc(String jobRoleId);

	long countByJobRoleIdAndJobRoleVersion(String jobRoleId, Integer jobRoleVersion);
}
