/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.dal;

import com.barraiser.commons.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "role_to_permission_mapping", schema = "authz")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleToPermissionMappingDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "role_id")
	private String roleId;

	@Column(name = "permission_id")
	private String permissionId;

	@Type(type = "jsonb")
	private String condition;
}
