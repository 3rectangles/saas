/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSCredentialRepository
		extends JpaRepository<ATSCredentialDAO, String> {
	Optional<ATSCredentialDAO> findByPartnerATSIntegrationId(final String partnerATSIntegrationId);

	List<ATSCredentialDAO> findAllByPartnerATSIntegrationIdIn(final List<String> partnerATSIntegrationIds);
}
