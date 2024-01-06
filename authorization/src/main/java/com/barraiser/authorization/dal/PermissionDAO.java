/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.dal;

import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.Resource;
import com.barraiser.commons.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "permission", schema = "authz")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PermissionDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "display_name")
	private String displayName;

	@Enumerated(EnumType.STRING)
	@Column(name = "action")
	private Action action;

	@Enumerated(EnumType.STRING)
	@Column(name = "resource")
	private Resource resource;

}
