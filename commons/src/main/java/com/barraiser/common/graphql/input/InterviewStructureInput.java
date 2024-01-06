/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.input.SkillInterviewingConfiguration.SkillInterviewingConfigurationInput;
import com.barraiser.common.graphql.types.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewStructureInput {
	private String id;
	private String name;
	private Integer roundIndex;
	private String domainId;
	private String round;
	private Integer duration;
	private String interviewStructureLink;
	private List<String> defaultQuestions;
	private List<CategoricalQuestionInput> defaultQuestionsWithCategories;
	private List<Document> defaultQuestionsDocuments;
	private List<SkillInterviewingConfigurationInput> skillInterviewingConfigurations;
	private List<String> categoryIds;
	private Integer expertJoiningTime;
	private List<SkillInput> specificSkills;
	private Boolean allSkillsFound;
	private Integer cutOffScore;
	private Integer thresholdScore;
	private Boolean requiresApproval;
	private CriteriaInput roundClearanceCriteria;
	private Money price;
	private Double margin;
	private Integer recommendationScore;
	private Boolean isBrRound;
	private String interviewFlowLink;
	private String atsId;
	private String interviewFlow;
	private Integer interviewCutoffScore;
	private List<CategoryCutoffInput> categoryCutoffs;
}
