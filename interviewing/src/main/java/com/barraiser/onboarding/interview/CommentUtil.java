/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.CommentDAO;
import com.barraiser.onboarding.dal.CommentHistoryDAO;
import com.barraiser.onboarding.dal.CommentHistoryRepository;
import com.barraiser.onboarding.dal.CommentRepository;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchDAO;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import com.barraiser.onboarding.interview.jira.dto.GenericIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.jira.dto.JiraEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class CommentUtil {
	private static final String JIRA = "JIRA";
	private static final String COMMENT_CREATED = "comment_created";
	private static final String COMMENT_UPDATED = "comment_updated";
	private static final String COMMENT_DELETED = "comment_deleted";
	public static final String BARRAISER_TEAM = "BarRaiser Team";
	public static final String LIKE_BARRAISER_COM = "@barraiser.com";
	private final JiraUUIDRepository jiraUUIDRepository;
	private final CommentRepository commentRepository;
	private final CommentHistoryRepository commentHistoryRepository;
	private final EvaluationSearchRepository evaluationSearchRepository;

	@Transactional
	public void actionCommentToDB(final JiraEvent event, final String action) {
		final Optional<JiraUUIDDAO> jiraUUIDDAO = this.jiraUUIDRepository.findByJira(event.getIssue());
		if (jiraUUIDDAO.isEmpty()) {
			log.error("JIRA mapping not present for: {}", event.getIssue());
			return;
		}
		final JiraCommentDTO jiraCommentDTO = event.getBody().getComment();
		if (jiraCommentDTO == null) {
			throw new IllegalArgumentException(String.format(
					"Comment from JIRA event for: %s", event.getIssue()));
		}
		final String entityId = jiraUUIDDAO.get().getUuid();
		final String entityType = getIssueTypeFromJiraEvent(event);
		switch (action) {
			case COMMENT_CREATED:
				saveCommentToDb(createComment(jiraCommentDTO, entityId, entityType, 1L));
				saveCommentHistoryToDb(createCommentHistory(jiraCommentDTO, entityId, entityType, 1L));
				break;
			case COMMENT_UPDATED:
				final Optional<CommentDAO> prevComment = this.commentRepository.findByCommentId(jiraCommentDTO.getId());
				if (prevComment.isPresent()) {
					saveCommentToDb(
							prevComment.get().toBuilder()
									.comment(jiraCommentDTO.getBody().toString())
									.updatedOn(jiraCommentDTO.getUpdated().toInstant())
									.commentVersion(prevComment.get().getCommentVersion() + 1)
									.isInternalNote(!jiraCommentDTO.getJsdPublic())
									.build());
					saveCommentHistoryToDb(createCommentHistory(jiraCommentDTO, entityId, entityType,
							prevComment.get().getCommentVersion() + 1));
				} else {
					saveCommentToDb(createComment(jiraCommentDTO, entityId, entityType, 1L));
					saveCommentHistoryToDb(createCommentHistory(jiraCommentDTO, entityId, entityType, 1L));
				}
				break;
			case COMMENT_DELETED:
				this.commentRepository.deleteByCommentId(jiraCommentDTO.getId());
				break;
			default:
				log.error("Incorrect event for comments");
		}
	}

	public void updateHaveQueryForPartnerFlagInEvaluationSearch(final String evaluationId) {
		final EvaluationSearchDAO evaluationSearchDAO = this.evaluationSearchRepository.findById(evaluationId).get();
		this.evaluationSearchRepository.save(
				evaluationSearchDAO.toBuilder()
						.haveQueryForPartner(false)
						.build());
	}

	private CommentHistoryDAO createCommentHistory(final JiraCommentDTO jiraCommentDTO, final String entityId,
			final String entityType,
			final Long commentVersion) {
		return CommentHistoryDAO.builder()
				.id(UUID.randomUUID().toString())
				.source(JIRA)
				.commentId(jiraCommentDTO.getId())
				.entityId(entityId)
				.entityType(entityType)
				.comment(jiraCommentDTO.getBody().toString())
				.commentedBy(getCommenter(jiraCommentDTO))
				.createdOn(Instant.now())
				.updatedOn(Instant.now())
				.isInternalNote(!jiraCommentDTO.getJsdPublic())
				.commentVersion(commentVersion)
				.build();
	}

	private CommentDAO createComment(final JiraCommentDTO jiraCommentDTO, final String entityId,
			final String entityType,
			final Long commentVersion) {
		return CommentDAO.builder()
				.id(UUID.randomUUID().toString())
				.source(JIRA)
				.commentId(jiraCommentDTO.getId())
				.entityId(entityId)
				.entityType(entityType)
				.comment(jiraCommentDTO.getBody().toString())
				.commentedBy(getCommenter(jiraCommentDTO))
				.createdOn(jiraCommentDTO.getCreated().toInstant())
				.updatedOn(jiraCommentDTO.getUpdated().toInstant())
				.commentVersion(commentVersion)
				.isInternalNote(!jiraCommentDTO.getJsdPublic())
				.build();
	}

	private String getCommenter(final JiraCommentDTO commentDTO) {
		if (commentDTO.getAuthor().getEmailAddress() == null || commentDTO.getAuthor().getEmailAddress().isBlank()) {
			if ("Automation".equalsIgnoreCase(commentDTO.getAuthor().getDisplayName())) {
				final String email = parseCommentForEmail(commentDTO.getBody().toString());
				return email.contains(LIKE_BARRAISER_COM)
						? BARRAISER_TEAM
						: email;
			} else {
				return BARRAISER_TEAM;
			}
		} else {
			return commentDTO.getAuthor().getEmailAddress().contains(LIKE_BARRAISER_COM)
					? BARRAISER_TEAM
					: commentDTO.getAuthor().getEmailAddress();
		}
	}

	private String parseCommentForEmail(final String comment) {
		final String[] commentLines = comment.split("\n");
		return commentLines[0].subSequence(1, commentLines[0].length() - 1).toString();
	}

	private void saveCommentToDb(final CommentDAO comment) {
		this.commentRepository.save(comment);
	}

	private void saveCommentHistoryToDb(final CommentHistoryDAO commentHistory) {
		this.commentHistoryRepository.save(commentHistory);
	}

	private String getIssueTypeFromJiraEvent(final JiraEvent event) {
		final GenericIssue issue = event.getBody().getIssue();
		final String[] splitValue = issue.getFields().getIssuetype().getName().split(" ");
		return splitValue[0];
	}
}
