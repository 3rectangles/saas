/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_comment")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCommentDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "entity_id")
	private String entityId;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "comment_value")
	private String commentValue;

	@Column(name = "reaction_value")
	private String reactionValue;

	@Column(name = "type")
	private String type;

	@Column(name = "offset_time")
	private Long offsetTime;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "deleted_on")
	private Instant deletedOn;

	@Column(name = "deleted_by")
	private String deletedBy;
}
