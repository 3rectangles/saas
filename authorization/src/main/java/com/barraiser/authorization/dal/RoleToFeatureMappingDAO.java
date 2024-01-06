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
import java.util.List;

@Entity
@Table(name = "role_to_feature_mapping", schema = "authz")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleToFeatureMappingDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "role_id")
	private String roleId;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "features_to_be_shown")
	private List<String> features;

}
