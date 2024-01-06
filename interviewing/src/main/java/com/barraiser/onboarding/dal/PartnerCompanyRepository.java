/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerCompanyRepository extends JpaRepository<PartnerCompanyDAO, String> {

	Optional<PartnerCompanyDAO> findByCompanyId(String companyId);

	List<PartnerCompanyDAO> findByPartnershipModelIdIn(final List<String> partnershipModelIds);
}
