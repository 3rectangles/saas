/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "interviewee_feedback")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class IntervieweeFeedbackDAO extends BaseModel {

	@Id
	private String interviewId;

	private String partnerId;

	private Integer interviewerKnowledge;

	private Integer interviewerClarityOfQuestions;

	private Integer feedbackFromInterviewer;

	private Integer structureOfInterview;

	private Integer qualityOfQuestions;

	private Double averageRating;

	private String anyOtherFeedback;

}
