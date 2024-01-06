/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewHistoryManager {
	private final InterviewHistoryRepository interviewHistoryRepository;
	private final ObjectMapper objectMapper;

	public List<InterviewHistoryDAO> getEarliestInterviewChangeHistoriesByField(
			final String interviewId, final String fieldName) {
		final Field field = this.getField(fieldName);
		final List<InterviewHistoryDAO> interviewChangeHistories = this.interviewHistoryRepository
				.findByInterviewIdAndCreatedOnIsNotNullOrderByCreatedOnAsc(interviewId);
		final List<InterviewHistoryDAO> interviewChangeHistoriesByField = new ArrayList<>();
		interviewChangeHistoriesByField.add(interviewChangeHistories.get(0));
		InterviewHistoryDAO previousHistory = interviewChangeHistories.get(0);
		for (final InterviewHistoryDAO interviewHistoryDAO : interviewChangeHistories) {
			if (!this.areFieldValuesEqual(previousHistory, interviewHistoryDAO, field)) {
				interviewChangeHistoriesByField.add(interviewHistoryDAO);
				previousHistory = interviewHistoryDAO;
			}
		}
		return interviewChangeHistoriesByField;
	}

	public InterviewHistoryDAO getLatestChangeInStatusOfInterview(
			final String interviewId, final String status) {
		return this.interviewHistoryRepository
				.findTopByInterviewIdAndStatusAndCreatedOnIsNotNullOrderByCreatedOnDesc(
						interviewId, status);
	}

	public List<InterviewHistoryDAO> getLatestInterviewChangeHistoriesByInterviewerIdAndField(
			final String partnerId,
			final String interviewerId, final String fieldName) {
		List<InterviewHistoryDAO> interviewHistoryDAOs;

		if (partnerId != null) {
			if (interviewerId != null) {
				interviewHistoryDAOs = this.interviewHistoryRepository
						.findAllByInterviewerIdAndPartnerId(interviewerId, partnerId);
			} else {
				interviewHistoryDAOs = this.interviewHistoryRepository
						.findAllByPartnerId(partnerId);
			}
		} else {
			interviewHistoryDAOs = this.interviewHistoryRepository
					.findAllByInterviewerId(interviewerId);
		}

		interviewHistoryDAOs.sort(
				Comparator.comparing(InterviewHistoryDAO::getCreatedOn).reversed());
		return this.filterHistoriesByFirstOccurrenceOfField(interviewHistoryDAOs, fieldName);
	}

	public List<InterviewDAO> mapInterviewHistoriesToInterviews(
			final List<InterviewHistoryDAO> interviewHistoryDAOs) {
		return interviewHistoryDAOs.stream()
				.map(
						x -> this.objectMapper.convertValue(x, InterviewDAO.class).toBuilder()
								.id(x.getInterviewId())
								.build())
				.collect(Collectors.toList());
	}

	private List<InterviewHistoryDAO> filterHistoriesByFirstOccurrenceOfField(
			final List<InterviewHistoryDAO> interviewHistoryDAOs, final String fieldName) {
		final Map<String, List<InterviewHistoryDAO>> mapOfInterviews = new HashMap<>();
		for (final InterviewHistoryDAO interviewHistoryDAO : interviewHistoryDAOs) {
			if (mapOfInterviews.containsKey(interviewHistoryDAO.getInterviewId())) {
				final List<InterviewHistoryDAO> interviewHistoryDAOsOfAnInterview = mapOfInterviews.getOrDefault(
						interviewHistoryDAO.getInterviewId(), new ArrayList<>());
				if (!this.containsFieldValue(
						interviewHistoryDAOsOfAnInterview, interviewHistoryDAO, fieldName)) {
					interviewHistoryDAOsOfAnInterview.add(interviewHistoryDAO);
					mapOfInterviews.put(
							interviewHistoryDAO.getInterviewId(),
							interviewHistoryDAOsOfAnInterview);
				}
			} else {
				mapOfInterviews.put(
						interviewHistoryDAO.getInterviewId(),
						new ArrayList<>(List.of(interviewHistoryDAO)));
			}
		}
		return mapOfInterviews.values().stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private boolean containsFieldValue(
			final List<InterviewHistoryDAO> interviewHistoryDAOsOfAnInterview,
			final InterviewHistoryDAO interviewHistoryDAO,
			final String fieldName) {

		final Field field = this.getField(fieldName);
		boolean hasSameFieldValue = false;
		for (final InterviewHistoryDAO interviewHistoryDAO2 : interviewHistoryDAOsOfAnInterview) {
			if (this.areFieldValuesEqual(interviewHistoryDAO, interviewHistoryDAO2, field)) {
				hasSameFieldValue = true;
				break;
			}
		}
		return hasSameFieldValue;
	}

	private Field getField(final String fieldName) {
		try {
			final Field field = InterviewHistoryDAO.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	private boolean areFieldValuesEqual(
			final InterviewHistoryDAO interviewHistory1,
			final InterviewHistoryDAO interviewHistory2,
			final Field field) {
		try {
			return field.get(interviewHistory1).equals(field.get(interviewHistory2));
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}

	public InterviewHistoryDAO getLatestByFieldValueAndReschedulingCount(
			final String interviewId, final String status, final Integer rescheduleCount) {
		return this.interviewHistoryRepository
				.findTopByInterviewIdAndRescheduleCountAndStatusAndCreatedOnIsNotNullOrderByCreatedOnDesc(
						interviewId, rescheduleCount, status);
	}

	public List<InterviewHistoryDAO> getLatestInterviewChangeHistoriesByInterviewerIdAndField(
			final Specification<InterviewHistoryDAO> specifications, final String fieldName) {
		final List<InterviewHistoryDAO> interviewHistoryDAOs = this.interviewHistoryRepository.findAll(specifications);
		interviewHistoryDAOs.sort(
				Comparator.comparing(InterviewHistoryDAO::getCreatedOn).reversed());
		return this.filterHistoriesByFirstOccurrenceOfField(interviewHistoryDAOs, fieldName);
	}

	public InterviewHistoryDAO getLatestInterviewChangeHistoryByRescheduleCountAndInterviewerId(
			final String interviewId, final Integer rescheduleCount, final String interviewerId) {
		return this.interviewHistoryRepository
				.findTopByInterviewIdAndRescheduleCountAndInterviewerIdOrderByCreatedOnDesc(
						interviewId, rescheduleCount, interviewerId);
	}
}
