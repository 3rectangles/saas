/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.enums.UserCommentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserComment {
	private String id;
	private String commentValue;
	private String reactionValue;
	private UserCommentType type;
	private Long offsetTime;
	private Long createdOn;
	private Long updatedOn;
	private List<UserComment> justifications;
	private UserDetails createdBy;
}
