/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated(forRemoval = true)
public interface CurrencyRepository extends JpaRepository<CurrencyDAO, String> {

	List<CurrencyDAO> findAllByDisabledOnIsNull();
}
