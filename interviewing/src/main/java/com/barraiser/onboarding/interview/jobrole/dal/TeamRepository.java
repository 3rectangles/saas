/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamDAO, String> {

	Optional<TeamDAO> findByAtsIdAndPartnerId(String atsId, String partnerId);

	List<TeamDAO> findAllByIdIn(List<String> ids);

	List<TeamDAO> findAllByPartnerId(String partnerId);

	Optional<TeamDAO> findByPartnerIdAndName(String partnerId, String name);

}
