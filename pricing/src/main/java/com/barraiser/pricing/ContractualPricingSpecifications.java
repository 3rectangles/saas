/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.pricing.dal.ContractualPricingConfigDAO;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class ContractualPricingSpecifications {

	public static Specification<ContractualPricingConfigDAO> isApplicable(final Instant applicableFrom,
			final Instant applicableTill) {
		return ((root, query, cb) -> cb.and(
				cb.or(cb.isNull(root.get("applicableFrom")),
						cb.lessThanOrEqualTo(root.get("applicableFrom"), applicableFrom)),
				cb.or(cb.isNull(root.get("applicableTill")),
						cb.greaterThan(root.get("applicableTill"), applicableTill))));
	}

	public static Specification<ContractualPricingConfigDAO> isActive() {
		return isApplicable(Instant.now(), Instant.now());
	}
}
