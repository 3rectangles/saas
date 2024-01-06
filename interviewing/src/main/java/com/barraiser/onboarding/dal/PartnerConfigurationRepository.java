/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PartnerConfigurationRepository extends JpaRepository<PartnerConfigurationDAO, String> {

	Optional<PartnerConfigurationDAO> findFirstByPartnerId(String partnerId);

	Optional<PartnerConfigurationDAO> findFirstByPartnerIdOrderByCreatedOnDesc(String partnerId);
}
