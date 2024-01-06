/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "partner_reps")
public class PartnerRepsDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_rep_id")
	private String partnerRepId;

	@Column(name = "partnerId")
	private String partnerId;

	@Column(name = "teams")
	private String teams;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "partner_roles")
	private List<String> partnerRoles;

	@Column(name = "locations")
	private String locations;

	@Column(name = "disabled_on")
	private Instant disabled_on;

	@Column(name = "creation_source")
	private String creationSource;

	@Column(name = "creation_source_meta")
	private String creationSourceMeta;
}
