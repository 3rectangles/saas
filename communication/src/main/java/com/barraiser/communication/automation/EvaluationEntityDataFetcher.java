/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Interview;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationEntityDataFetcher {

	private final static String GET_INTERVIEW_QUERY = "query getInterviews($input: GetInterviewsInput!) {\n" +
			"    getInterviews(input: $input) {\n" +
			"        evaluation {\n" +
			"            id\n" +
			"        }\n" +
			"    }\n" +
			"}";

	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;

	public Entity getEvaluationEntity(final Entity interviewEntity) {

		final Object queryData = this.queryDataFetcher.fetchQueryData(GET_INTERVIEW_QUERY, interviewEntity);

		final Interview interview = this.objectMapper.convertValue(
				this.queryDataFetcher.getObjectFromPath(queryData, List.of("getInterviews", "0")), Interview.class);
		return interviewEntity.toBuilder()
				.type(EntityType.EVALUATION)
				.id(interview.getEvaluation().getId())
				.build();
	}

}
