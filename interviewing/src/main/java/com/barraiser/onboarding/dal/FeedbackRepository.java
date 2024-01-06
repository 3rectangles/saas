/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackDAO, String> {

	/**
	 * TBD: More thought can be given, but kept return type feedbackDAO not list
	 * as this is used to get overallFeedback. There should be only once
	 * overallFeedback
	 * per interview
	 */

	List<FeedbackDAO> findByReferenceId(String referenceId);

	void deleteByReferenceIdIn(List<String> QuestionId);

	List<FeedbackDAO> findByReferenceIdIn(List<String> QuestionId);

	void deleteByIdIn(List<String> feedbackId);

	List<FeedbackDAO> findAllByReferenceIdInAndType(List<String> referenceIds, String type);

	List<FeedbackDAO> findAllByReferenceIdAndType(String referenceId, String type);

	FeedbackDAO findByReferenceIdAndTypeAndRescheduleCount(String id, String overallFeedbackTypeStrength,
			Integer rescheduleCount);

	List<FeedbackDAO> findAllByReferenceIdAndTypeAndRescheduleCount(String id, String overallFeedbackTypeSoftSkills,
			Integer rescheduleCount);

	void deleteByReferenceIdAndTypeAndRescheduleCount(String id, String overallFeedbackTypeSoftSkills,
			Integer rescheduleCount);

	List<FeedbackDAO> findAllByReferenceId(String referenceId);
}
