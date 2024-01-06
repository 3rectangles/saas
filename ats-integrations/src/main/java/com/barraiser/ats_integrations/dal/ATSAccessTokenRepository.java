/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ATSAccessTokenRepository
		extends JpaRepository<ATSAccessTokenDAO, String> {
	Optional<ATSAccessTokenDAO> findByPartnerIdAndAtsProvider(String partnerId, String atsProvider);
}
