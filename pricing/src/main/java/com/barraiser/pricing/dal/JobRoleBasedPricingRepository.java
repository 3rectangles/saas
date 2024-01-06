/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRoleBasedPricingRepository extends JpaRepository<JobRoleBasedPricingDAO, String> {
	List<JobRoleBasedPricingDAO> findAllByJobRoleIdAndInterviewStructureIdOrderByCreatedOnDesc(String jobRoleId,
			String interviewStructureId);

	List<JobRoleBasedPricingDAO> findAllByJobRoleIdInOrderByCreatedOnDesc(List<String> jobRoleIds);

}
