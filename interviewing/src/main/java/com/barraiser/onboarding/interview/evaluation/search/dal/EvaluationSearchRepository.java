/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationSearchRepository
		extends JpaRepository<EvaluationSearchDAO, String>, JpaSpecificationExecutor<EvaluationSearchDAO> {
	List<EvaluationSearchDAO> findByCompanyId(String companyId);

	@Query(value = "SELECT count(*) FROM evaluation_search WHERE job_role_id = (:id)", nativeQuery = true)
	Long getCountOfTotalEvaluationsForJobRole(@Param("id") String jobRoleId);

	// TODO: Steer away from using display Status
	@Query(value = "SELECT count(*) FROM evaluation_search WHERE job_role_id = (:id) and display_status != 'Cancelled' and display_status != 'BR Evaluated'", nativeQuery = true)
	Long getCountOfActiveEvaluationsForJobRole(@Param("id") String jobRoleId);

	@Query(value = "SELECT count(*) FROM evaluation_search WHERE job_role_id = (:id) and display_status = 'Requires Action'", nativeQuery = true)
	Long getCountOfRequiresActionEvaluationsForJobRole(@Param("id") String jobRoleId);

	@Query(value = "SELECT count(*) FROM evaluation_search WHERE company_id = (:id) and display_status != 'Cancelled' and deleted_on is null", nativeQuery = true)
	Long countAllNotCancelledByCompanyId(@Param("id") String companyId);
}
