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

@Entity
@Table(name = "highlight_question")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HighlightQuestionDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "highlight_id")
	private String highlightId;

	@Column(name = "description")
	private String description;

	@Column(name = "offset_time")
	private Integer offsetTime;

	@Column(name = "speaker")
	private String speaker;
}
