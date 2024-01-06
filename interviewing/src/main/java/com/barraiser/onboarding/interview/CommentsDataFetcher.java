/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.CommentsInput;
import com.barraiser.common.graphql.types.Comment;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.JiraComment;
import com.barraiser.common.utilities.EmailParser;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.CommentDAO;
import com.barraiser.onboarding.dal.CommentRepository;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO.JiraCommentBodyV3;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO.JiraCommentBodyV3.JiraCommentContentV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class CommentsDataFetcher implements MultiParentTypeDataFetcher<Object> {
	public static final String COMMENTS_FOR_ENTITY_DATA_LOADER = "COMMENTS_FOR_ENTITY_DATA_LOADER";
	private static final String TYPE_EVALUATION = "Evaluation";
	private static final String BARRAISER_TEAM = "BarRaiser Team";
	private static final Executor executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final JiraWorkflowManager jiraWorkflowManager;
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getComments"),
				List.of(TYPE_EVALUATION, "latestComment"));
	}

	public DataLoader<String, JiraComment> getCommentsForEntityDataLoader() {
		return DataLoader.newMappedDataLoader((Set<String> entityIdSet) -> CompletableFuture.supplyAsync(() -> {
			Map<String, JiraComment> jiraCommentMap = new HashMap<>();
			for (String id : entityIdSet) {
				Optional<CommentDAO> commentDAO = this.commentRepository
						.findFirstByEntityIdAndCommentedByLikeAndIsInternalNoteOrderByCreatedOnDesc(id,
								BARRAISER_TEAM, false);
				if (commentDAO.isPresent()) {
					jiraCommentMap.put(id, this.commentMapper.toJiraComment(commentDAO.get()));
				} else {
					log.error("Could not find comment to display for EntityId: {}", id);
					jiraCommentMap.put(id, null);
				}
			}
			return jiraCommentMap;
		}, executor));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final CommentsInput input = this.graphQLUtil.getArgument(environment, Constants.CONTEXT_KEY_INPUT,
				CommentsInput.class);
		final GraphQLObjectType type = (graphql.schema.GraphQLObjectType) environment.getParentType();
		log.debug("Parent type : {}", type);

		// 1. Fetch comments from JIRA
		if (type.getName().equals(QUERY_TYPE)) {
			final List<JiraCommentDTO> jiraCommentDTOS = this.jiraWorkflowManager
					.getJiraIssueComments(input.getEntityId());

			// 2. Transform JIRA comments to generic comments
			final List<Comment> commentList = jiraCommentDTOS.stream()
					.filter(JiraCommentDTO::getJsdPublic)
					.map(this::parseComment)
					.collect(Collectors.toList());

			return DataFetcherResult.newResult()
					.data(commentList)
					.build();
		} else if (type.getName().equals(TYPE_EVALUATION)) {
			final DataLoader<String, List<Comment>> commentsForEntityIdListDataLoader = environment
					.getDataLoader(COMMENTS_FOR_ENTITY_DATA_LOADER);
			final Evaluation evaluation = environment.getSource();
			return commentsForEntityIdListDataLoader.load(evaluation.getId());
		} else {
			throw new IllegalArgumentException("Bad parent type while accessing Comment type, please fix your query");
		}
	}

	Comment parseComment(final JiraCommentDTO comment) {
		StringBuilder author = new StringBuilder();
		final JiraCommentBodyV3 body = this.objectMapper.convertValue(comment.getBody(),
				JiraCommentDTO.JiraCommentBodyV3.class);
		boolean authorFound = false;
		final String firstStringInBody = body.getContent().get(0).getType().equals("paragraph") &&
				body.getContent().get(0).getContent().size() > 0 &&
				"text".equals(body.getContent().get(0).getContent().get(0).getType())
						? body.getContent().get(0).getContent().get(0).getText()
						: "";
		if (firstStringInBody.length() > 0 && firstStringInBody.charAt(0) == '[' && firstStringInBody.endsWith("]")) {
			author.append(firstStringInBody, 1, firstStringInBody.length() - 1);
			try {
				EmailParser.validateEmail(author.toString());
				authorFound = true;
				body.getContent().get(0).getContent().remove(0);
			} catch (final Exception ignored) {
				author = new StringBuilder();
			}
		}

		if (!authorFound) {
			if ("customer".equals(comment.getAuthor().getAccountType())) {
				author.append(comment.getAuthor().getEmailAddress());
			} else {
				author.append("BarRaiser Team");
			}
		}
		final StringBuilder bodyString = new StringBuilder();
		body.getContent().forEach(childContent -> {
			bodyString.append(this.parseCommentContent(childContent, 0));
			bodyString.append("\n");
		});
		return Comment.builder()
				.comment(bodyString.toString())
				.author(author.toString())
				.createdOn(comment.getCreated().toString())
				.updatedOn(comment.getUpdated().toString())
				.build();
	}

	String parseCommentContent(final JiraCommentContentV3 content, final Integer contentDepth) {
		// allow the depth of content only till 6 levels
		if (contentDepth >= 6) {
			return "";
		}
		switch (content.getType()) {
			case "text":
				final StringBuilder link = new StringBuilder();
				if (content.getMarks() != null) {
					content.getMarks().forEach(mark -> {
						if ("link".equals(mark.getType())) {
							link.append("<a href=\"" + ((LinkedHashMap) mark.getAttrs()).get("href").toString() + "\">"
									+ content.getText() + "</a>");
						}
					});
				}
				return link.length() == 0 ? content.getText() : link.toString();
			case "hardBreak":
				return "<p></p>";
			case "mention":
				return "<span class=\"commentTag\">" + ((LinkedHashMap) content.getAttrs()).get("text").toString()
						+ "</span>";
			case "paragraph":
			case "mediaGroup":
			case "mediaSingle":
				final StringBuilder text = new StringBuilder();
				if ("paragraph".equals(content.getType())) {
					text.append("<p>");
				}
				content.getContent().forEach(childContent -> {
					text.append(this.parseCommentContent(childContent, contentDepth + 1));
					text.append(" ");
				});
				if ("paragraph".equals(content.getType())) {
					text.append("</p>");
				}
				return text.toString();
			case "media":
				return "[attachment]";
			case "inlineCard":
				final String url = ((LinkedHashMap) content.getAttrs()).get("url").toString();
				return "<a href=\"" + url + "\">" + url + "</a>";
			case "bulletList":
				final StringBuilder bulletedList = new StringBuilder("<ul>");
				content.getContent().forEach(
						childContent -> bulletedList.append(this.parseCommentContent(childContent, contentDepth + 1)));
				bulletedList.append("</ul>");
				return bulletedList.toString();
			case "listItem":
				final StringBuilder listItem = new StringBuilder("<li>");
				content.getContent().forEach(
						childContent -> listItem.append(this.parseCommentContent(childContent, contentDepth + 1)));
				listItem.append("</li>");
				return listItem.toString();

			default:
				return "";
		}
	}
}
