/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmartRecruitersWebhookRequestBody {
	@JsonProperty("job_id")
	private String jobId;

	@JsonProperty("candidate_id")
	private String candidateId;

	@JsonProperty("application_id")
	private String applicationId;
}
