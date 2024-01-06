/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewerFeedbackInput {
	private String interviewerId;
	private String feedback;
	private List<String> ccUserList;
	private Integer offsetTime;
	private String interviewId;
}
