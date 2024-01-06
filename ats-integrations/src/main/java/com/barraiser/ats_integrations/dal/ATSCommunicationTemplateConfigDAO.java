/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.ats_integrations.calendar_interception.dto.VariableRegexMapping;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ats_communication_template_config")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSCommunicationTemplateConfigDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ats_provider")
	private ATSProvider atsProvider;

	@Column(name = "event_type")
	private String eventType;

	@Column(name = "channel")
	private String channel;

	@Column(name = "recepient")
	private String recepient;

	@Column(name = "body_template")
	private String bodyTemplate;

	@Column(name = "body_replacement_template")
	private String bodyReplacementTemplate;

	@Column(name = "subject_template")
	private String subjectTemplate;

	@Column(name = "subject_replacement_template")
	private String subjectReplacementTemplate;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "variable_regex_mapping")
	private List<VariableRegexMapping> variableRegexMapping;

}
