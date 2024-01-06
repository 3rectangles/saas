/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.amazonaws.services.forecast.model.Domain;
import com.barraiser.common.graphql.types.SkillInterviewingConfiguration.SkillInterviewingConfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewStructure {
	private String id;
	private String name;
	private List<SkillInterviewingConfiguration> skillInterviewingConfigurations;
	private Integer duration;
	private Domain domain;
	private String domainId;
	private List<String> defaultQuestions;
	private String link;
	private Integer expertJoiningTime;
	private String interviewFlowLink;
	private Boolean allSkillsFound;
	private Boolean isBrRound;
	private String interviewFlow;
	private String feedbackTextType;
	private String inputType;

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public void setFeedbackTextType(String feedbackTextType) {
		this.feedbackTextType = feedbackTextType;
	}
}
