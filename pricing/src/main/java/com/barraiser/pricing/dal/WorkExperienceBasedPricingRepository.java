/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import com.barraiser.common.enums.RoundType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkExperienceBasedPricingRepository extends JpaRepository<WorkExperienceBasedPricingDAO, String> {

	List<WorkExperienceBasedPricingDAO> findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
			String partnerId, RoundType roundType, Integer workExperienceOfCandidateInMonths);
}
