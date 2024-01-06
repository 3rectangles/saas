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
@Table(name = "default_question")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DefaultQuestionsDAO extends BaseModel {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "interview_structure_id")
	private String interviewStructureId;

	@Column(name = "question")
	private String question;

	@Column(name = "category_id")
	private String categoryId;

	@Column(name = "question_type")
	private String questionType;

	@Column(name = "is_preinterview_question")
	private Boolean isPreInterviewQuestion;
}
