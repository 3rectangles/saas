/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

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
@Table(name = "comment")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentDAO extends BaseModel {
	@Id
	private String id;
	@Column(name = "source")
	private String source;
	@Column(name = "comment_id")
	private String commentId;
	@Column(name = "comment_version")
	private Long commentVersion;
	@Column(name = "entity_id")
	private String entityId;
	@Column(name = "entity_type")
	private String entityType;
	@Column(name = "comment")
	private String comment;
	@Column(name = "commented_by")
	private String commentedBy;
	@Column(name = "is_internal_note")
	private Boolean isInternalNote;
}
