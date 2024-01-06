/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerATSIntegrationRepository
		extends JpaRepository<PartnerATSIntegrationDAO, String> {

	List<PartnerATSIntegrationDAO> findAllByPartnerId(final String partnerId);

	List<PartnerATSIntegrationDAO> findAllByPartnerIdIn(final List<String> partnerIds);

	Optional<PartnerATSIntegrationDAO> findByPartnerIdAndAtsProvider(
			final String partnerId,
			final String atsProvider);
}
