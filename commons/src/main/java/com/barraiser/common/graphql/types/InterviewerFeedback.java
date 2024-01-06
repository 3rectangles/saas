/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewerFeedback {
	private String id;
	private UserDetails user;
	private String feedback;
	private UserDetails sender;
	private Long createdOn;
}
