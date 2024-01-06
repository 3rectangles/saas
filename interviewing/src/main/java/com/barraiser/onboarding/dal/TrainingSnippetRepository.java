/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.graphql.types.training.JobRoleWithSnippetCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingSnippetRepository extends JpaRepository<TrainingSnippetDAO, String> {

	@Query("select ts from TrainingSnippetDAO ts inner join TrainingJobRoleMappingDAO trm on trm.trainingSnippetId = ts.id"
			+
			" where trm.jobRoleId = :jobRoleId order by ts.createdOn desc")
	List<TrainingSnippetDAO> findByJobRoleId(@Param("jobRoleId") String jobRoleId);

	@Query("select new com.barraiser.common.graphql.types.training.JobRoleWithSnippetCount(trm.jobRoleId, count(trm)) from TrainingSnippetDAO ts"
			+
			" inner join TrainingJobRoleMappingDAO trm on trm.trainingSnippetId = ts.id where ts.partnerId = :partnerId "
			+
			" group by trm.jobRoleId")
	List<JobRoleWithSnippetCount> findJobRoleCountByPartnerId(@Param("partnerId") String partnerId);

	void deleteByIdAndPartnerId(String id, String partnerId);
}
