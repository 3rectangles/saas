/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.graphql.types.ApplicableFilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilterRepository extends JpaRepository<FilterDAO, String> {

	List<FilterDAO> findAllByFilterContextAndFilterType(String filterContext,
			ApplicableFilterType applicableFilterType);

	Optional<FilterDAO> findByFilterContextAndNameAndFilterType(String filterContext, String name,
			ApplicableFilterType applicableFilterType);

	List<FilterDAO> findAllByFilterContextAndFilterTypeOrderBySequenceNumber(String filterContext,
			ApplicableFilterType applicableFilterType);

	List<FilterDAO> findAllByFilterContextAndDefaultValueNotNull(String filterContext);
}
