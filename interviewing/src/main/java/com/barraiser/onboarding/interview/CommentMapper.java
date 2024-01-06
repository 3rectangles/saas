/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.JiraComment;
import com.barraiser.onboarding.dal.CommentDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentMapper {

	public JiraComment toJiraComment(final CommentDAO commentDAO) {
		return JiraComment.builder()
				.id(commentDAO.getId())
				.comment(commentDAO.getComment())
				.updatedOn(commentDAO.getUpdatedOn().getEpochSecond())
				.build();
	}
}
