/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.dal.InterviewStructureToExpertsDAO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.repository.InterviewStructureToExpertsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetEligibleInterviewersForInternalInterviewsProcessor implements MatchInterviewersProcessor {

	private final InterviewStructureToExpertsRepository interviewStructureToExpertsRepository;

	private final ExpertRepository expertRepository;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {

		this.getEligibleInterviewers(data);
	}

	private void getEligibleInterviewers(final MatchInterviewersData data) {

		final Optional<InterviewStructureToExpertsDAO> interviewStructureToExpertsDAO = this.interviewStructureToExpertsRepository
				.findByInterviewStructureId(data.getInterviewStructureId());

		if (interviewStructureToExpertsDAO.isPresent()) {
			log.info("An interview panel is configured for interview structure : {}", data.getInterviewStructureId());
			this.getPanelExperts(interviewStructureToExpertsDAO.get(), data);
		} else {
			log.info("No interview panel is configured for interview structure : {}", data.getInterviewStructureId());
			this.getAllExpertsForPartner(data);
		}

		data.setInterviewersId(
				data.getInterviewers().stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	private void getPanelExperts(final InterviewStructureToExpertsDAO interviewStructureToExpertsDAO,
			final MatchInterviewersData data) {
		final List<ExpertDAO> experts = this.expertRepository
				.findAllByIdIn(new HashSet<>(interviewStructureToExpertsDAO.getEligibleExperts()));

		final List<InterviewerData> expertsData = experts.stream()
				.map(e -> {
					return InterviewerData.builder()
							.id(e.getId())
							.build();
				})
				.collect(Collectors.toList());

		data.setInterviewers(expertsData);

	}

	private void getAllExpertsForPartner(final MatchInterviewersData data) {
		final List<InterviewerData> expertsData = this.expertRepository.findAllByTenantId(data.getPartnerCompanyId())
				.stream()
				.map(e -> {
					return InterviewerData.builder()
							.id(e.getId())
							.build();
				})
				.collect(Collectors.toList());

		data.setInterviewers(expertsData);
	}

}
