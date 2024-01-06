/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DyteMeetingRepository extends JpaRepository<DyteMeetingDAO, String> {
	DyteMeetingDAO findByInterviewIdAndRescheduleCount(String interviewId, Integer rescheduleCount);
}
