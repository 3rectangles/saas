/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class InterviewService {
	private final InterViewRepository interViewRepository;
	private final InterviewHistoryRepository interviewHistoryRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public InterviewDAO save(final InterviewDAO interviewDAO, final String createdBy, final String source) {
		final InterviewDAO savedInterview = this.interViewRepository.save(interviewDAO);
		this.interviewHistoryRepository.save(
				this.objectMapper.convertValue(savedInterview, InterviewHistoryDAO.class).toBuilder()
						.id(UUID.randomUUID().toString())
						.interviewId(savedInterview.getId())
						.createdBy(createdBy)
						.source(source)
						.build());
		return savedInterview;
	}

	@Transactional
	public List<InterviewDAO> saveAll(final List<InterviewDAO> interviewDAOList, final String createdBy,
			final String source) {
		final List<InterviewDAO> savedInterviewList = this.interViewRepository.saveAll(interviewDAOList);
		savedInterviewList.forEach(savedInterview -> this.interviewHistoryRepository.save(
				this.objectMapper.convertValue(savedInterview, InterviewHistoryDAO.class).toBuilder()
						.id(UUID.randomUUID().toString())
						.interviewId(savedInterview.getId())
						.createdBy(createdBy)
						.source(source)
						.build()));
		return savedInterviewList;
	}

	public InterviewDAO save(final InterviewDAO interviewDAO) {
		return this.save(interviewDAO, null, null);
	}

	public InterviewDAO save(final InterviewDAO interviewDAO, final String createdBy) {
		return this.save(interviewDAO, createdBy, null);
	}

	public List<InterviewDAO> saveAll(final List<InterviewDAO> interviewDAOList) {
		return this.saveAll(interviewDAOList, null, null);
	}

	public InterviewDAO findById(final String interviewId) {
		return this.interViewRepository.findById(interviewId).get();
	}

	public List<InterviewDAO> findAllByEvaluationId(final String evaluationId) {
		return this.interViewRepository.findAllByEvaluationId(evaluationId);
	}
}
