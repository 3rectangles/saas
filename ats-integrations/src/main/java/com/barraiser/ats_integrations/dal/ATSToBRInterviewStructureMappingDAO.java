/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "ats_to_br_interview_structure_mapping")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSToBRInterviewStructureMappingDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ats_provider")
	private ATSProvider atsProvider;

	@Column(name = "br_interview_structure_id")
	private String brInterviewStructureId;

	@Column(name = "ats_interview_structure_id")
	private String atsInterviewStructureId;
}
