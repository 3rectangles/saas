/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "communication_template_config")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunicationTemplateConfigDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "event_type")
	private String eventType;

	@Enumerated(EnumType.STRING)
	@Column(name = "channel")
	private Channel channel;

	@Enumerated(EnumType.STRING)
	@Column(name = "recipient_type")
	private RecipientType recipientType;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "template_rule")
	private String templateRule;

	@Column(name = "enabled")
	private Boolean enabled;
}
