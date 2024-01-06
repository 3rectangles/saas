/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewStructureSkillsRepository
		extends JpaRepository<InterviewStructureSkillsDAO, String> {

	void deleteByInterviewStructureId(String interviewStructureId);

	List<InterviewStructureSkillsDAO> findAllByInterviewStructureIdAndIsSpecific(
			String interviewStructureId, boolean isSpecific);

	List<InterviewStructureSkillsDAO> findAllByInterviewStructureId(
			String interviewStructureId);

}
