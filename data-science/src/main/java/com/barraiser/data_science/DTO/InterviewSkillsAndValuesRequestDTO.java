/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSkillsAndValuesRequestDTO {
	@JsonProperty("free_text")
	private String freeText;

	@JsonProperty("job_role_name")
	private String jobRoleName;

	@JsonProperty("department")
	private String department;

	@JsonProperty("rounds")
	private Integer noOfRounds;
}
