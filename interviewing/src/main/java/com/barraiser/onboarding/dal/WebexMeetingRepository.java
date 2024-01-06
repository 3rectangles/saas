/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebexMeetingRepository extends JpaRepository<WebexMeetingDAO, String> {
	WebexMeetingDAO findByInterviewIdAndRescheduleCount(String interviewId, Integer rescheduleCount);
}
