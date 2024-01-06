/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSPartnerRepMappingRepository extends JpaRepository<ATSPartnerRepMappingDAO, String> {

	Optional<ATSPartnerRepMappingDAO> findByBrPartnerRepIdAndPartnerId(final String partnerRepId,
			final String partnerId);

	List<ATSPartnerRepMappingDAO> findByPartnerId(final String partnerId);

	Optional<ATSPartnerRepMappingDAO> findByBrPartnerRepId(final String brPartnerRepId);

}
