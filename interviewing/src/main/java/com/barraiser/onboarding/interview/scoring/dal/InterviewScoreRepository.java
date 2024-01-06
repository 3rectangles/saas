/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.scoring.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScoreRepository extends JpaRepository<InterviewScoreDAO, String> {
	List<InterviewScoreDAO> findAllByInterviewIdIn(List<String> interviewIds);

	Optional<InterviewScoreDAO> findByInterviewIdAndSkillIdAndScoringAlgoVersion(String interviewId, String skillId,
			String scoringAlgoVersion);

	void deleteAllByInterviewIdAndScoringAlgoVersion(String interviewId, String scoringAlgoVersion);

	List<InterviewScoreDAO> findAllByInterviewIdAndScoringAlgoVersion(String interviewId,
			String defaultScoringAlgoVersion);
}
