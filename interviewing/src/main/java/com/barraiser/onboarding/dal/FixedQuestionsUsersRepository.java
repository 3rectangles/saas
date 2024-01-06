/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedQuestionsUsersRepository extends JpaRepository<FixedQuestionsUsersDAO, String> {
	List<FixedQuestionsUsersDAO> findByQuestionIdIn(List<String> questionIds);
}