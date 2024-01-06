/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Evaluation {
	private String id;
	private String partnerId;
	private String jira;
	private String status;
	private StatusType barraiserStatus;
	private StatusType partnerStatus;
	private String jobRoleId;
	private Integer jobRoleVersion;
	private String candidateId;
	private Long createdOn;
	private String pocEmail;
	private Long bgsCreatedTimeEpoch;
	private String waitingReasonId;
	private String cancellationReasonId;
	private Double bgs;
	private String scoringAlgoVersion;
	private Double partnerBGS;
	private Double overallBGS;
	private String displayStatus;
	private Integer percentile;
	private Interviewee candidate;
	private List<UserDetails> pocs;
	private String defaultScoringAlgoVersion;
	private EvaluationRecommendation recommendation;
	private JiraComment latestComment;
	private Boolean isEvaluationScoreUnderReview;
	private String defaultRecommendationVersion;
	private Integer scaleBGS; // any int 4,5,10,600,800
	private Integer scaleScoring; // possible values: 4,5,10
}
