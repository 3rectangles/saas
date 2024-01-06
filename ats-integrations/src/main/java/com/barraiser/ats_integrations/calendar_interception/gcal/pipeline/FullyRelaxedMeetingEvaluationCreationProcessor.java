/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.EvaluationManagementFeignClient;
import com.barraiser.ats_integrations.common.client.JobRoleManagementFeignClient;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.commons.dto.evaluationManagement.AddEvaluationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@Component
@Log4j2
public class FullyRelaxedMeetingEvaluationCreationProcessor implements SchedulingProcessing {
	private final EvaluationManagementFeignClient evaluationManagementFeignClient;
	private final JobRoleManagementFeignClient jobRoleManagementFeignClient;
	private static final String SOURCE = "%s_CAL_INTERCEPTION";

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		this.createEvaluation(data);
	}

	public void createEvaluation(final SchedulingData data) {
		data.setBrEvaluationId(UUID.randomUUID().toString());
		this.addEvaluationInBarraiserSystem(data);
	}

	private void addEvaluationInBarraiserSystem(final SchedulingData data) {

		final AddEvaluationRequest addEvaluationRequest = AddEvaluationRequest.builder()
				.evaluationId(data.getBrEvaluationId())
				.candidateName(data.getBrCalendarEvent().getSummary())
				.isCandidateAnonymous(Boolean.TRUE)
				.partnerId(data.getPartnerId())
				.pocEmails(data.getPocEmails())
				.source(String.format(SOURCE, data.getPartnerId()))
				.jobRoleId(data.getBrJobRoleId())
				.build();

		this.evaluationManagementFeignClient.addEvaluation(addEvaluationRequest);

	}
}
