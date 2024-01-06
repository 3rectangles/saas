/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InterViewRepository
		extends JpaRepository<InterviewDAO, String>, JpaSpecificationExecutor<InterviewDAO> {

	List<InterviewDAO> findAllByIntervieweeIdAndStatus(
			final String intervieweeId, final String status);

	List<InterviewDAO> findAllByIntervieweeId(final String intervieweeId);

	List<InterviewDAO> findAllByEvaluationId(final String evaluationId);

	List<InterviewDAO> findAllByEvaluationIdIn(List<String> evaluationIds);

	InterviewDAO findByZoomLinkLike(String s);

	InterviewDAO findByMeetingLinkLike(String s);

	InterviewDAO findByStatusNotInAndMeetingLinkContainingIgnoreCase(List<String> status, String meetingURL);

	List<InterviewDAO> findAllByCancellationTimeIsNull();

	List<InterviewDAO> findAllByStatusAndInterviewerIdIn(
			String status, List<String> interviewerIds);

	List<InterviewDAO> findAllByStartDateLessThanAndZoomEndTimeGreaterThan(
			Long endDateTime, Long startDateTime);

	List<InterviewDAO> findAllByEvaluationIdAndInterviewStructureId(
			String evaluationId, String interviewStructureId);

	InterviewDAO findByRescheduledFrom(String jira);

	List<InterviewDAO> findAllByStartDateGreaterThanEqualAndEndDateLessThanEqual(
			Long startDate, Long endDate);

	List<InterviewDAO> findAllByIdIn(List<String> interviewIds);

	List<InterviewDAO> findAllByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotAndTaggingAgentIsNull(
			List<String> roundTypes, Long startDate, Long endDate, String status);

	long countByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotAndTaggingAgentIsNull(
			List<String> roundTypes, Long startDate, Long endDate, String status);

	List<InterviewDAO> findAllByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNot(
			List<String> roundTypes, Long startDate, Long endDate, String status);

	long countByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNot(
			List<String> roundTypes, Long startDate, Long endDate, String status);

	List<InterviewDAO> findByEvaluationIdAndInterviewStructureId(String evaluationId, String interviewStructureId);

	List<InterviewDAO> findAllByEvaluationIdInAndInterviewerIdAndStatusNotIn(
			List<String> evaluationIds, String interviewerId, List<String> value);

	List<InterviewDAO> findAllByEvaluationIdAndInterviewRound(String evaluationId, String roundType);

	InterviewDAO findTopByInterviewRoundNotInAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusNotInAndTaggingAgentIsNullAndIsTaggingAgentNeeded(
			List<String> roundTypesThatNeedNoTaggingAgent, Long effectiveStartDate, Long endDate,
			List<String> statusToBeSkipped, Boolean aTrue);

	long countByPartnerId(String partnerId);

	List<InterviewDAO> findAllyByPartnerId(String partnerId);
}
