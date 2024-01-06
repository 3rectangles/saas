/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchedulingSessionRepository extends JpaRepository<SchedulingSessionDAO, String> {

	Optional<SchedulingSessionDAO> findTopByInterviewIdAndRescheduleCountOrderByCreatedOnDesc(String id,
			Integer rescheduleCount);
}
