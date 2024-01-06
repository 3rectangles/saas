/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyteParticipantRepository extends JpaRepository<DyteParticipantDAO, String> {

	List<DyteParticipantDAO> findAllByMeetingId(final String meetingId);

	DyteParticipantDAO findByMeetingIdAndParticipantId(String meetingId, String participantId);
}
