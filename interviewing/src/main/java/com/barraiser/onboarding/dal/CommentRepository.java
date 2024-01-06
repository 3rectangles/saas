/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentDAO, String> {

	Optional<CommentDAO> findFirstByEntityIdAndCommentedByLikeAndIsInternalNoteOrderByCreatedOnDesc(String entityId,
			String commentedBy, Boolean isInternalNote);

	Optional<CommentDAO> findByCommentId(String commentId);

	void deleteByCommentId(String commentId);
}
