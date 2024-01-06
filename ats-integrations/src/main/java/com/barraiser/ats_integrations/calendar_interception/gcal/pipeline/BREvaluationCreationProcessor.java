/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.EvaluationManagementFeignClient;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;

import com.barraiser.commons.dto.evaluationManagement.AddEvaluationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@AllArgsConstructor
@Component
public class BREvaluationCreationProcessor implements SchedulingProcessing {

	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final EvaluationManagementFeignClient evaluationManagementFeignClient;

	private static final String SOURCE = "%s_CAL_INTERCEPTION";

	@Override
	public void process(SchedulingData data) throws IOException {
		this.createEvaluation(data);
	}

	public void createEvaluation(final SchedulingData data) {

		log.info("Creating evaluation for partner : {} , ats provider : {} ", data.getPartnerId(),
				data.getAtsProvider().getValue());

		final Optional<ATSToBREvaluationDAO> atsToBREvaluationDAOOptional = this.atsToBREvaluationRepository
				.findByAtsEvaluationId(data.getAtsEvaluationId());

		if (atsToBREvaluationDAOOptional.isEmpty()) {
			data.setBrEvaluationId(UUID.randomUUID().toString());
			this.addEvaluationInBarraiserSystem(data);
		} else {
			data.setBrEvaluationId(atsToBREvaluationDAOOptional.get().getBrEvaluationId());
		}

	}

	private void addEvaluationInBarraiserSystem(final SchedulingData data) {

		final String candidateName = data.getCandidateDetails().getFirstName() + " " + (data
				.getCandidateDetails().getLastName() == null ? "" : data.getCandidateDetails().getLastName());

		final AddEvaluationRequest addEvaluationRequest = AddEvaluationRequest.builder()
				.evaluationId(data.getBrEvaluationId())
				.candidateName(candidateName)
				.isCandidateAnonymous(Boolean.TRUE)
				.jobRoleId(data.getBrJobRoleId())
				.partnerId(data.getPartnerId())
				.pocEmails(data.getPocEmails())
				.source(String.format(SOURCE, data.getAtsProvider().getValue()))
				.candidateEmail(data.getCandidateDetails().getEmailId())
				.candidatePhone(data.getCandidateDetails().getMobileNumber())
				.resumeLink(data.getCandidateDetails().getResumeLink())
				.isATSEvaluation(Boolean.TRUE)
				.build();

		this.evaluationManagementFeignClient.addEvaluation(addEvaluationRequest);

		this.atsToBREvaluationRepository.save(
				ATSToBREvaluationDAO.builder()
						.id(UUID.randomUUID().toString())
						.partnerId(data.getPartnerId())
						.atsProvider(data.getAtsProvider().getValue())
						.atsEvaluationId(data.getAtsEvaluationId())
						.brEvaluationId(data.getBrEvaluationId())
						.remoteData(data.getRemoteData())
						.build());

	}

}
