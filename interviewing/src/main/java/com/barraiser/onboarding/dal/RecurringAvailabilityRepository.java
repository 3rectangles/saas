/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RecurringAvailabilityRepository extends JpaRepository<RecurringAvailabilityDAO, Long> {

	@Transactional
	void deleteByUserId(final String userId);

	List<RecurringAvailabilityDAO> findByUserId(final String userId);
}
