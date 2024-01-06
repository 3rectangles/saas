/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefaultQuestionsRepository extends JpaRepository<DefaultQuestionsDAO, String> {

	void deleteAllByInterviewStructureId(String id);

	List<DefaultQuestionsDAO> findAllByInterviewStructureId(String id);

	List<DefaultQuestionsDAO> findByInterviewStructureIdOrderByCreatedOnAsc(String interviewId);
}
