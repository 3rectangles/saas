/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.firestore.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FirestoreFeedbackDocV1 {
	private Long version;
	private List<String> questionIds;
	private OverallFeedback overallFeedback;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Question {
		private String id;
		private List<String> feedbackIds;
		private String question;
		private String questionTagged;
		private String categoryPredicted;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Feedback {
		private String id;
		private String questionId;
		private String categoryId;
		private FeedbackText feedback;
		private Long rating;
		private String difficulty;
		private String feedbackWeightage;
		private String categoryName;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class OverallFeedback {
		private Feedback areasOfImprovement;
		private Feedback strength;
		private Feedback overallFeedback;
		private List<Feedback> softSkills;
		private InterviewerRecommendation interviewerRecommendation;
		private Boolean wasInterviewerVideoOn;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class FeedbackText {
		private String sessionId;
		private String value;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class InterviewerRecommendation {
		private String cheatingRemarks;
		private Boolean cheatingSuspected;
		private Long hiringRating;
		private Boolean interviewIncomplete;
		private String interviewIncompleteRemarks;
		private String remarks;
	}
}
