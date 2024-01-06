/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewersSortingProcessor implements MatchInterviewersProcessor {
	public static final String INTERVIEWERS_SORTING_VERSION = "interviewers-sorting-version";

	private final List<InterviewerSorter> interviewerSorters;
	private final DynamicAppConfigProperties appConfigProperties;
	private final ObjectMapper objectMapper;
	private final EvaluationRepository evaluationRepository;

	@Override
	public void process(final MatchInterviewersData data) {
		log.info("start sorting for interview_id : {}", data.getInterviewId());
		this.sorting(data);
	}

	private void sorting(final MatchInterviewersData data) {
		final String sortingVersion = this.getSortingVersion();
		final InterviewerSorter sortingAlgo = this.interviewerSorters.stream()
				.filter(x -> x.version().equals(sortingVersion))
				.findAny()
				.get();
		for (final InterviewersPerDayData interviewersPerDayData : data.getInterviewersPerDayDataList()) {
			List<InterviewerData> interviewers = interviewersPerDayData.getInterviewers();
			final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(data.getEvaluationId()).get();
			Boolean isDemoEvaluation = evaluationDAO.getIsDemo() != null && evaluationDAO.getIsDemo();
			interviewers = sortingAlgo.sort(interviewers, isDemoEvaluation, data.getIsFallbackEnabled());
			try {
				log.info(
						"sorted interviewers for interview_id: {} date : {} , interviewers : {} ",
						data.getInterviewId(),
						interviewersPerDayData.getDate(),
						this.objectMapper.writeValueAsString(interviewers));
			} catch (final Exception ignored) {
			}
			interviewers = this.combineOriginalAndDuplicateInterviewers(interviewers, data.getDuplicateExperts());
			interviewersPerDayData.setInterviewers(interviewers);
		}
	}

	private List<InterviewerData> combineOriginalAndDuplicateInterviewers(
			final List<InterviewerData> originalInterviewers,
			final List<InterviewerData> duplicateInterviewers) {
		final List<InterviewerData> interviewers = new ArrayList<>();
		for (final InterviewerData interviewer : originalInterviewers) {
			interviewers.add(interviewer);
			interviewers.addAll(
					duplicateInterviewers.stream()
							.filter(x -> interviewer.getId().equals(x.getDuplicatedFrom()))
							.collect(Collectors.toList()));
		}
		return interviewers;
	}

	private String getSortingVersion() {
		return this.appConfigProperties.getString(INTERVIEWERS_SORTING_VERSION);
	}
}
