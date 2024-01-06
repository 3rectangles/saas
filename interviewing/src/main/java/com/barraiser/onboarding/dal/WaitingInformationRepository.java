/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingInformationRepository
		extends JpaRepository<WaitingInformationDAO, String>,
		JpaSpecificationExecutor<WaitingInformationDAO> {
}
