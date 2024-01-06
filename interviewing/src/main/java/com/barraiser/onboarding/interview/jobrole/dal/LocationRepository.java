/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationDAO, String> {

	Optional<LocationDAO> findByAtsIdAndPartnerId(String atsId, String partnerId);

	List<LocationDAO> findAllByIdIn(List<String> ids);

	List<LocationDAO> findAllByPartnerId(String partnerId);

	Optional<LocationDAO> findByPartnerIdAndName(String partnerId, String name);
}
