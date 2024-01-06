/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<StatusDAO, String> {
	List<StatusDAO> findAllByPartnerIdInAndEntityType(List<String> partnerIds, String entityType);

	Optional<StatusDAO> findTopByPartnerIdAndEntityType(String partnerIds, String entityType);

	Optional<StatusDAO> findByInternalStatusAndPartnerIdAndEntityType(String internalStatus, String partnerId,
			String entityType);

	@Query("SELECT s FROM status s WHERE s.entityType = 'evaluation' AND (s.partnerId IS NULL OR s.partnerId = 'BarRaiser')")
	List<StatusDAO> findAllDefaultEvaluationStatus();

	List<StatusDAO> findAllByEntityType(String interview);

	@Query(value = "SELECT * FROM status s WHERE s.entity_type = 'evaluation' AND (s.partner_id IS NULL OR s.partner_id = 'BarRaiser') AND s.internal_status=:internal_status", nativeQuery = true)
	Optional<StatusDAO> findDefaultEvaluationStatusForInternalStatus(@Param("internal_status") String internalStatus);

	List<StatusDAO> findAllByEntityTypeAndContext(String entityType, String context);

	List<StatusDAO> findAllByIdIn(List<String> ids);

	Optional<StatusDAO> findByInternalStatusAndEntityType(String internalStatus, String entityType);
}
