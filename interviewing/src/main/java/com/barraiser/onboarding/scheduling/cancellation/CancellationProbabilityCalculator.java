/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.common.requests.CancellationPredictionRequest;
import com.barraiser.common.responses.CancellationPredictionResponse;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dataScience.DataScienceFeignClient;
import com.barraiser.onboarding.interview.InterviewUtil;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class CancellationProbabilityCalculator {
	private final EvaluationRepository evaluationRepository;
	private final JobRoleRepository jobRoleRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewUtil interviewUtil;
	private final DataScienceFeignClient dataScienceFeignClient;

	public Map<Long, Double> calculate(final InterviewDAO interviewDAO, final List<Long> slots) {
		final CancellationPredictionRequest cancellationPredictionRequest = this.constructRequest(interviewDAO, slots);
		List<CancellationPredictionResponse.SlotProbability> slotProbabilityList;
		try {
			slotProbabilityList = this.dataScienceFeignClient
					.getCancellationProbability(cancellationPredictionRequest)
					.getBody()
					.getSlotProbabilityList();
		} catch (final Exception e) {
			slotProbabilityList = slots.stream()
					.map(
							x -> CancellationPredictionResponse.SlotProbability
									.builder()
									.startTime(x)
									.cancellationProbability(null)
									.build())
					.collect(Collectors.toList());
			log.error(
					"cancellation prediction feign client failed for interview id : {}",
					interviewDAO.getId());
		}
		return slotProbabilityList.stream()
				.collect(
						HashMap::new,
						(m, v) -> m.put(v.getStartTime(), v.getCancellationProbability()),
						HashMap::putAll);
	}

	private CancellationPredictionRequest constructRequest(
			final InterviewDAO interviewDAO, final List<Long> slots) {
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository
				.findById(interviewDAO.getInterviewStructureId())
				.get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleRepository
				.findByEntityId(
						new VersionedEntityId(
								evaluationDAO.getJobRoleId(),
								evaluationDAO.getJobRoleVersion()))
				.get();
		return CancellationPredictionRequest.builder()
				.interview(
						CancellationPredictionRequest.InterviewData.builder()
								.interviewRound(interviewDAO.getInterviewRound())
								.domainId(interviewStructureDAO.getDomainId())
								.targetCompanyId(jobRoleDAO.getCompanyId())
								.duration(interviewStructureDAO.getDuration() * 60)
								.category(jobRoleDAO.getCategory())
								.roundNumber(
										this.interviewUtil.getRoundNumberOfInterview(interviewDAO))
								.build())
				.slots(
						slots.stream()
								.map(
										x -> CancellationPredictionRequest.Slot.builder()
												.startTime(x)
												.build())
								.collect(Collectors.toList()))
				.build();
	}
}
