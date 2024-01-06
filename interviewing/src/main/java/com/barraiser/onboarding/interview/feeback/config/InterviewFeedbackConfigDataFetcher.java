/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.config;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.AllowAllAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Component
public class InterviewFeedbackConfigDataFetcher extends AuthorizedGraphQLQuery_deprecated<String> {
	private final ObjectMapper objectMapper;
	private final InterviewUtil interviewUtil;
	private final InterviewStructureManager interviewStructureManager;

	private String saasConfigWithOldInterviewingFlow = null;
	private String saasConfigWithNewInterviewingFlow = null;
	private String iaasConfigWithOldInterviewingFlow = null;
	private String iaasConfigWithNewInterviewingFlow = null;

	public InterviewFeedbackConfigDataFetcher(
			final AllowAllAuthorizer authorizer,
			final ObjectFieldsFilter<String> objectFieldsFilter,
			final ObjectMapper objectMapper,
			final InterviewUtil interviewUtil,
			final InterviewStructureManager interviewStructureManager) {
		super(authorizer, objectFieldsFilter);
		this.objectMapper = objectMapper;
		this.interviewUtil = interviewUtil;
		this.interviewStructureManager = interviewStructureManager;
	}

	@Override
	protected String fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final Interview interview = environment.getSource();
		return this.fetchFeedbackConfigString(interview);
	}

	public JsonNode fetchFeedbackConfig(final Interview interview) throws JsonProcessingException {
		return this.objectMapper.readTree(this.fetchFeedbackConfigString(interview));
	}

	private String fetchFeedbackConfigString(final Interview interview) {
		if (this.interviewUtil.isSaasInterview(interview.getInterviewRound())) {
			return this.getSaasConfig(interview);
		}
		return this.getIaasConfig(interview);
	}

	private String getIaasConfig(final Interview interview) {
		if (this.interviewStructureManager.isInterviewStructureWithNewFlow(interview.getInterviewStructureId())) {
			return this.iaasConfigWithNewInterviewingFlow;
		}
		return this.iaasConfigWithOldInterviewingFlow;
	}

	private String getSaasConfig(final Interview interview) {
		if (this.interviewStructureManager.isInterviewStructureWithNewFlow(interview.getInterviewStructureId())) {
			return this.saasConfigWithNewInterviewingFlow;
		}
		return this.saasConfigWithOldInterviewingFlow;
	}

	private String readFile(final String filePath) {
		try {
			final InputStream is = InterviewFeedbackConfigDataFetcher.class
					.getResourceAsStream("/interview_feedback_configs/" + filePath);
			return new String(is.readAllBytes());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "feedbackConfig"));
	}

	@PostConstruct
	private void init() {
		this.saasConfigWithOldInterviewingFlow = this
				.readFile("saas_interview_feedback_config_with_old_interviewing_flow.json");
		this.saasConfigWithNewInterviewingFlow = this
				.readFile("saas_interview_feedback_config_with_new_interviewing_flow.json");
		this.iaasConfigWithOldInterviewingFlow = this
				.readFile("iaas_interview_feedback_config_with_old_interviewing_flow.json");

		this.iaasConfigWithNewInterviewingFlow = this
				.readFile("iaas_interview_feedback_config_with_new_interviewing_flow.json");
	}
}
