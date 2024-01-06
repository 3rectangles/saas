/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionDAO, String> {
	List<QuestionDAO> findAllByInterviewId(String interviewId);

	List<QuestionDAO> findAllByInterviewIdOrderByStartTimeEpochAsc(String interviewId);

	List<QuestionDAO> deleteAllByInterviewIdAndIsDefaultTrue(String interviewId);

	void deleteByIdIn(List<String> questionIds);

	List<QuestionDAO> findAllByInterviewIdIn(List<String> collect);

	List<QuestionDAO> findAllByInterviewIdInAndMasterQuestionIdIsNull(List<String> interviewIds);

	List<QuestionDAO> findAllByInterviewIdAndMasterQuestionIdNullAndRescheduleCountOrderByStartTimeEpochAsc(String id,
			Integer rescheduleCount);

	List<QuestionDAO> findAllByMasterQuestionIdAndRescheduleCountOrderByStartTimeEpochAsc(String id,
			Integer rescheduleCount);

	List<QuestionDAO> findAllByInterviewIdAndRescheduleCount(String id, Integer rescheduleCount);

	List<QuestionDAO> findAllByInterviewIdAndRescheduleCountOrderByStartTimeEpochAsc(String id,
			Integer rescheduleCount);

	List<QuestionDAO> findAllByIdIn(List<String> questionIds);
}
