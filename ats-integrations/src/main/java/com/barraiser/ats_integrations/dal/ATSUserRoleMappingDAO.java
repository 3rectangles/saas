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
@Table(name = "ats_user_role_mapping")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSUserRoleMappingDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "ats_provider")
	private String atsProvider;

	@Column(name = "br_user_role_id")
	private String brUserRoleId;

	@Column(name = "ats_user_role_id")
	private String atsUserRoleId;

	@Column(name = "ats_user_role_name")
	private String atsUserRoleName;

}
