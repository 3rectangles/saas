/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.dal;

import com.barraiser.common.enums.UserCommentType;
import com.barraiser.common.graphql.types.UserComment;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCommentsMapper {
	UserCommentRepository userCommentRepository;
	UserInformationManagementHelper userInformationManagementHelper;

	public UserCommentsMapper(UserCommentRepository userCommentRepository,
			UserInformationManagementHelper userInformationManagementHelper) {
		this.userCommentRepository = userCommentRepository;
		this.userInformationManagementHelper = userInformationManagementHelper;
	}

	public UserComment getUserComment(final UserCommentDAO userCommentDAO, List<UserComment> justifications) {
		return UserComment.builder()
				.id(userCommentDAO.getId())
				.commentValue(userCommentDAO.getCommentValue())
				.reactionValue(userCommentDAO.getReactionValue())
				.type(UserCommentType.valueOf(userCommentDAO.getType()))
				.offsetTime(userCommentDAO.getOffsetTime())
				.createdBy(this.userInformationManagementHelper.getUserDetailsById(userCommentDAO.getCreatedBy()))
				.createdOn(userCommentDAO.getCreatedOn().toEpochMilli())
				.updatedOn(userCommentDAO.getUpdatedOn().toEpochMilli())
				.justifications(justifications)
				.build();
	}
}
