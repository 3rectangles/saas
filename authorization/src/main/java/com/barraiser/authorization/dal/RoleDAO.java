/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.dal;

import com.barraiser.commons.dal.BaseModel;
import com.barraiser.commons.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "role", schema = "authz")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "name")
	private String name; // TBD:can be removed

	@Column(name = "display_name")
	private String displayName;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private RoleType type;
}
