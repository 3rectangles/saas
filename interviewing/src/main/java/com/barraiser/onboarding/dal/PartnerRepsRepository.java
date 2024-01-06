/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerRepsRepository
		extends JpaRepository<PartnerRepsDAO, String>, JpaSpecificationExecutor<PartnerRepsDAO> {

	List<PartnerRepsDAO> findAllByPartnerId(String partnerId);

	Optional<PartnerRepsDAO> findByPartnerRepIdAndPartnerId(String partnerRepId, String partnerId);

	List<PartnerRepsDAO> findAllByPartnerRepId(String userId);

	Optional<PartnerRepsDAO> findTopByPartnerRepId(String userName);

	Optional<PartnerRepsDAO> findByPartnerRepId(String userName);

	List<PartnerRepsDAO> findAllByIdIn(List<String> ids);
}
