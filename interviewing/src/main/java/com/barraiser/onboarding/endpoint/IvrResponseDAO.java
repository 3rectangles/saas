/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ivr_response")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class IvrResponseDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "message_bird_flow_id")
	private String messageBirdFlowId;

	@Column(name = "phone")
	private String phone;

	@Column(name = "call_answered")
	private Boolean callAnswered;

	@Column(name = "ivr_response")
	private Boolean ivrResponse;

}
