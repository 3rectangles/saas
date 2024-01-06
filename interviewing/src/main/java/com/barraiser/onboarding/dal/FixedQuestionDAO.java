/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@EntityListeners(AuditListener.class)
@Entity
@Getter
@Table(name = "fixed_questions")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FixedQuestionDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "question")
	private String question;

	@Column(name = "solution")
	private Long solution;

	@Column(name = "added_by_user_id")
	private String addedBy;

	@Column(name = "valid_flag")
	private Boolean valid;
}
