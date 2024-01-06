/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "whatsapp_template")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WhatsappTemplateDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "query")
	private String query;

	@Column(name = "message_bird_template_name")
	private String messageBirdTemplateName;

	@Column(name = "message_bird_template_variables")
	private String messageBirdTemplateVariables;
}
