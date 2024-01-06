/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingTagRepository extends JpaRepository<TrainingTagDAO, String> {

	List<TrainingTagDAO> findAllByNameIn(List<String> nameList);

	List<TrainingTagDAO> findByNameContainingIgnoreCaseAndPartnerId(String tag, String partnerId);
}
