/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.graphql.types.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("atsPartnerConfigurationRepository")
public interface ATSPartnerConfigurationRepository
		extends JpaRepository<PartnerConfigurationDAO, String> {

	Optional<PartnerConfigurationDAO> findFirstByPartnerIdOrderByCreatedOnDesc(String partnerId);

	List<PartnerConfigurationDAO> findAllByDeletedOnIsNull();

}
