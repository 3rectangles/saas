/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input.SkillInterviewingConfiguration;

import com.barraiser.common.graphql.types.Document;
import com.barraiser.common.graphql.types.Domain;
import com.barraiser.common.graphql.types.Skill;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class SkillInterviewingConfigurationInput {
	private String id;

	private Integer version;

	private String partnerId;

	private Domain domain;

	private Skill skill;

	private Integer duration;

	private String questioningType;

	private String categoryCoverage;

	private String barraisingExpectations;

	private String mandatoryExpectations;

	private String sampleQuestions;

	private List<Document> sampleQuestionDocuments;

	private String roleSpecificInstructions;
}
