/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.pojo;

import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.onboarding.dal.QuestionDAO;
import lombok.Data;

import java.util.List;

@Data
public class InterviewQuestionDetails {
	private String interviewId;
	private List<String> questionIds;
	private List<QuestionDAO> questions;
	private List<InterviewCategory> interviewCategories;
}
