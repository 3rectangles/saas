/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

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
@Table(name = "ats_interview_to_candidate_mapping")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSInterviewToCandidateMappingDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "ats_interview_id")
	private String atsInterviewId;

	@Column(name = "ats_candidate_id")
	private String atsCandidateId;

	@Column(name = "ats_provider")
	private String atsProvider;
}
