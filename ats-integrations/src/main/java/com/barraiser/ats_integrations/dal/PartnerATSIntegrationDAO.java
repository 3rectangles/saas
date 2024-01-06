/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "partner_ats_integration")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartnerATSIntegrationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "ats_provider")
	private String atsProvider;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "ats_aggregator")
	private ATSAggregator atsAggregator;

	@Column(name = "ats_provider_displayable_name")
	private String atsProviderDisplayableName;
}
