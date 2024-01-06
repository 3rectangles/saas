/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RelaxedMeetingInterceptionConfigRepository
		extends JpaRepository<RelaxedMeetingInterceptionConfigDAO, String> {

	Optional<RelaxedMeetingInterceptionConfigDAO> findByPartnerId(String partnerId);

}
