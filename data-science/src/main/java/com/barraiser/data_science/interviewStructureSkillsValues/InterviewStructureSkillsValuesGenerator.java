/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.interviewStructureSkillsValues;

import com.barraiser.common.graphql.input.GetJobRoleSkillsValuesInput;
import com.barraiser.common.graphql.types.GetJobRoleSkillsAndValues;
import com.barraiser.data_science.DTO.InterviewSkillsAndValuesRequestDTO;
import com.barraiser.data_science.DataScienceCacheManager;
import com.barraiser.data_science.InterviewStructureSkillsValuesFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Log4j2
@Component
public class InterviewStructureSkillsValuesGenerator {
	private final DataScienceCacheManager dataScienceCacheManager;
	private final InterviewStructureSkillsValuesFeignClient interviewStructureSkillsValuesFeignClient;

	public GetJobRoleSkillsAndValues generateInterviewStructureSkillsValues(
			GetJobRoleSkillsValuesInput input) throws Exception {

		String response = this.dataScienceCacheManager
				.getResponseFromCache(
						input,
						String.class);

		if (response != null) {
			return GetJobRoleSkillsAndValues.builder()
					.response(response)
					.build();
		}

		final Long startTime = System.currentTimeMillis();
		// TODO: Remove Logs
		log.info(String.format("Time before Get Skills DS API Call: %s For Input: %s", startTime,
				input.getJobRoleDescription()));

		response = this.interviewStructureSkillsValuesFeignClient
				.getInterviewStructureSkillsValues(InterviewSkillsAndValuesRequestDTO.builder()
						.freeText(input.getJobRoleDescription())
						.jobRoleName(input.getJobRoleName())
						.department(input.getDepartment())
						.noOfRounds(input.getNoOfRounds())
						.build())
				.getBody();

		log.info("Time taken for Get Skills DS API Call: " + (System.currentTimeMillis() - startTime));

		this.dataScienceCacheManager
				.addResponseToCache(input, response);

		return GetJobRoleSkillsAndValues.builder()
				.response(response)
				.build();
	}
}
