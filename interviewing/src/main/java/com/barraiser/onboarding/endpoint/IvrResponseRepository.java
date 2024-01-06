/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IvrResponseRepository extends JpaRepository<IvrResponseDAO, String> {
	Optional<IvrResponseDAO> findTopByPhoneAndMessageBirdFlowIdAndCreatedOnIsNotNullOrderByCreatedOnDesc(String phone,
			String messageBirdFlowId);
}
