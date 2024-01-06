/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

public enum UserCommentType {
	COMMENT("COMMENT"), REACTION("REACTION");

	private final String userCommentType;

	UserCommentType(final String userCommentType) {
		this.userCommentType = userCommentType;
	}

	public String getValue() {
		return this.userCommentType;
	}

}
