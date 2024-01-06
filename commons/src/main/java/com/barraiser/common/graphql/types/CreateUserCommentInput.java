/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.enums.UserCommentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateUserCommentInput {

	private String entityType;
	private String entityId;
	private String commentValue;
	private String reactionValue;
	private UserCommentType type;
	private Long offsetTime;
	private String context;

}
