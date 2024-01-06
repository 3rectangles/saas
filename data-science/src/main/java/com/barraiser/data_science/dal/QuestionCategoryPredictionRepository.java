/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionCategoryPredictionRepository
		extends JpaRepository<QuestionCategoryPredictionDAO, String> {
}
