/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;

import com.barraiser.commons.auth.SearchFilter;

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
@Table(name = "evaluation_access_filter")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationAccessFilterDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "partner_id")
	private String partnerId;

	@Type(type = "jsonb")
	@Column(name = "filter", columnDefinition = "jsonb")
	private SearchFilter filter;
}
