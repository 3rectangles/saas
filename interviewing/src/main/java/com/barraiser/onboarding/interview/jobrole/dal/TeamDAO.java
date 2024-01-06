/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "team")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamDAO extends BaseModel {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "ats_id")
	private String atsId;

	@Column(name = "partner_id")
	private String partnerId;

	@Enumerated(EnumType.STRING)
	@Column(name = "ats_provider")
	private ATSProvider atsProvider;

	@Column(name = "creation_source")
	private String creationSource;

	@Column(name = "creation_source_meta")
	private String creationSourceMeta; // Used to store information related to creation like maybe createdFrom what

}
