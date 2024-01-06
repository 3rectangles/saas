/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationReasonRepository
		extends JpaRepository<CancellationReasonDAO, String>, JpaSpecificationExecutor<CancellationReasonDAO> {

	Optional<List<CancellationReasonDAO>> findByCancellationTypeInAndIsActiveTrueAndProcessType(
			List<String> cancellationType, String processType);

	Optional<CancellationReasonDAO> findByIdAndProcessType(String id, String processType);

	List<CancellationReasonDAO> findAllByCancellationType(String type);

	List<CancellationReasonDAO> findAllByCancellationTypeIn(List<String> type);
}
