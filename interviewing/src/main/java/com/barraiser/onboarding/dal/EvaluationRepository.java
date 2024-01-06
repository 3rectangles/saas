/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository
		extends JpaRepository<EvaluationDAO, String>, JpaSpecificationExecutor<EvaluationDAO> {

	List<EvaluationDAO> findAllByCandidateIdInAndJobRoleIdAndStatusNotInAndDeletedOnIsNull(
			List<String> candidateIds, String jobRoleId, List<String> status);

	List<EvaluationDAO> findAllByPartnerIdAndCandidateIdInAndStatusNotInAndDeletedOnIsNull(
			String partnerId, List<String> candidateIds, List<String> status);

	List<EvaluationDAO> findAllByJobRoleIdInAndStatus(List<String> jobRoleIds, final String status);

	List<EvaluationDAO> findAllByStatus(String status);

	List<EvaluationDAO> findAllByIdIn(List<String> evaluationIds);

	List<EvaluationDAO> findAllByCandidateId(String candidateId);

	List<EvaluationDAO> findAllByPartnerId(String candidateId);

	long countAllByCompanyIdAndDeletedOnIsNull(String companyId);

	Optional<EvaluationDAO> findByIdAndDeletedOnIsNull(String evaluationId);

	List<EvaluationDAO> findAllByJobRoleIdIn(List<String> jobRoleIds, Pageable pageable);
}
