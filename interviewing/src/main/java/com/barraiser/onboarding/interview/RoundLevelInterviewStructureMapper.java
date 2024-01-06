/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.InterviewStructure;
import com.barraiser.common.graphql.types.RoundLevelInterviewStructure;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.barraiser.common.graphql.input.CategoryCutoffInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class RoundLevelInterviewStructureMapper {
	public RoundLevelInterviewStructure toRoundLevelInterviewStructure(
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO)
			throws JsonMappingException, JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		List<CategoryCutoffInput> categoryCutoffs = null;
		if (jobRoleToInterviewStructureDAO.getCategoryRejectionJSON() != null) {
			mapper.readValue(
					jobRoleToInterviewStructureDAO.getCategoryRejectionJSON(),
					new TypeReference<List<CategoryCutoffInput>>() {
					});
		}
		return RoundLevelInterviewStructure
				.builder()
				.id(jobRoleToInterviewStructureDAO.getId())
				.interviewRound(jobRoleToInterviewStructureDAO.getInterviewRound())
				.interviewStructure(InterviewStructure
						.builder()
						.id(jobRoleToInterviewStructureDAO.getInterviewStructureId())
						.build())
				.cutOffScore(jobRoleToInterviewStructureDAO.getAcceptanceCutoffScore())
				.thresholdScore(jobRoleToInterviewStructureDAO.getRejectionCutoffScore())
				.problemStatementLink(jobRoleToInterviewStructureDAO.getProblemStatementLink())
				.recommendationScore(jobRoleToInterviewStructureDAO.getRecommendationScore())
				.requiresApproval(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases())
				.interviewCutoffScore(jobRoleToInterviewStructureDAO.getInterviewCutoffScore())
				.categoryCutoffs(categoryCutoffs)
				.build();
	}
}
