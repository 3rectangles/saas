/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.dal;

import com.barraiser.ats_integrations.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "interviewer_feedback")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class InterviewerFeedbackDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "feedback")
	private String feedback;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "tagged_users_list")
	private List<String> mailList;

	@Column(name = "feedback_provider_user_id")
	private String feedbackProviderUserId;

	@Column(name = "interviewer_id")
	private String interviewerId;

	@Column(name = "offset_time")
	private Integer offsetTime;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

}
