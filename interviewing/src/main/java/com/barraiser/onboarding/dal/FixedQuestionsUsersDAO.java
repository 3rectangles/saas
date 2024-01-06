/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "fixed_question_expert")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FixedQuestionsUsersDAO extends BaseModel {

	@Id
	private String id;

	private String questionId;
	private String expertId;
	private Boolean validFlag;

}
