/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "user_details_snapshot")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDetailsSnapShotDAO extends BaseModel {

	@Id
	private String id;

	private String entityId;

	private String entityType;

	@Type(type = "jsonb")
	@Column(name = "payload", columnDefinition = "jsonb")
	private Object payload;
}
