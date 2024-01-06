/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.dal;

import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_to_role_mapping", schema = "authz")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserToRoleMappingDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "role_id")
	private String roleId;

	@Column(name = "authorization_dimension")
	@Enumerated(EnumType.STRING)
	private Dimension authorizationDimension;

	@Column(name = "authorization_dimension_value")
	private String authorizationDimensionValue;

	@Column(name = "updated_by")
	private String updatedBy;

	private Instant deletedOn;
}
