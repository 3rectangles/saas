/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.firestore.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewFlow {
	private String version;
	private List<Section> sections;
	private OverallFeedback overallFeedback;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Section {
		private Skill skill;
		private Integer duration;
		private Boolean isEvaluative;
		private String guidelines;
		private List<Question> questions;
		private List<String> sampleProblems;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Skill {
		private String id;
		private String name;
		private String parentSkillId;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Question {
		private String name;
		private String weightage;
		private String type;
		private Integer rating;
		private String feedback;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class OverallFeedback {
		private List<SoftSkill> softSkills;
		private Integer overallRating;
		private String strengths;
		private String areasOfImprovement;
		private String overallFeedback;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class SoftSkill {
		private String id;
		private String name;
		private String weightage;
		private Integer rating;
	}
}
