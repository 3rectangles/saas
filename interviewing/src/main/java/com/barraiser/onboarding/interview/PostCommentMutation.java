/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.CommentsInput;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.commons.auth.UserRole;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Component
public class PostCommentMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final EvaluationStatusManager evaluationStatusManager;
	private final EvaluationRepository evaluationRepository;
	private final CommentUtil commentUtil;
	private final Authorizer authorizer;
	private final EvaluationManager evaluationManager;

	private final String CLIENT_COMMENTED_PRIORITY_FLAG = "Client_commented";

	@Override
	public String name() {
		return "postComment";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);

		final CommentsInput input = this.graphQLUtil.getArgument(environment, Constants.CONTEXT_KEY_INPUT,
				CommentsInput.class);

		final String prefixedComment = String.format("[%s]\n%s", user.getEmail(), input.getComment());

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(input.getEntityId()).get();

		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationDAO.getId());

		this.isAuthorizedToPostComment(user, partnerId);

		this.jiraWorkflowManager.addCommentInJira(input.getEntityId(),
				JiraCommentDTO.builder().body(prefixedComment).build());

		if (user.getRoles().contains(UserRole.PARTNER)) {
			if (evaluationDAO.getStatus().equals(EvaluationStatus.WAITING_CLIENT.getValue())) {
				this.evaluationStatusManager.transitionBarRaiserStatus(evaluationDAO.getId(),
						EvaluationStatus.CLIENT_REPLIED.getValue(), user.getUserName());
				this.jiraWorkflowManager.transitionJiraStatus(evaluationDAO.getId(),
						EvaluationStatus.CLIENT_REPLIED.getValue());
				this.commentUtil.updateHaveQueryForPartnerFlagInEvaluationSearch(evaluationDAO.getId());
			}
			this.setPriorityFlagOnJira(evaluationDAO.getId());
		}

		return DataFetcherResult.newResult()
				.data(true)
				.build();
	}

	private void setPriorityFlagOnJira(final String evaluationId) {
		final EvaluationServiceDeskIssue currentIssue = this.jiraWorkflowManager.getEvaluationIssue(evaluationId);
		final List<String> priorityFlags = Objects.requireNonNullElse(currentIssue.getFields().getPriorityFlags(),
				new ArrayList<>());
		if (!priorityFlags.contains(CLIENT_COMMENTED_PRIORITY_FLAG)) {
			priorityFlags.add(CLIENT_COMMENTED_PRIORITY_FLAG);
		}
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags)
				.build();

		this.jiraWorkflowManager.setEvaluationFieldsInJira(evaluationId, updatedFields);
	}

	private void isAuthorizedToPostComment(final AuthenticatedUser authenticatedUser,
			final String partnerId) {
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(partnerId)
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, authorizationResource);
	}
}
