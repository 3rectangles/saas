/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractualPricingConfigRepository extends JpaRepository<ContractualPricingConfigDAO, String>,
		JpaSpecificationExecutor<ContractualPricingConfigDAO> {

	List<ContractualPricingConfigDAO> findAllByPartnerIdInOrderByCreatedOnDesc(List<String> partnerIds);

	List<ContractualPricingConfigDAO> findAllByPartnerId(String partnerId);
}
