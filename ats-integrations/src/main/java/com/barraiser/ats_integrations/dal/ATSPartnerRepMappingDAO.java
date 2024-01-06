/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "ats_partner_reps_mapping")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSPartnerRepMappingDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ats_provider")
	private ATSProvider atsProvider;

	@Column(name = "br_partner_rep_id")
	private String brPartnerRepId;

	@Column(name = "ats_partner_rep_id")
	private String atsPartnerRepId;

	@Column(name = "ats_user_name")
	private String atsUserName;

}
