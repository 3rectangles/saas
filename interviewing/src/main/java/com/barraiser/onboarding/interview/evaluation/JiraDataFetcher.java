/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.graphql.GraphQLQuery;

import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Component
public class JiraDataFetcher implements GraphQLQuery<String> {
	private final JiraUUIDRepository jiraUUIDRepository;

	@Override
	public String name() {
		return "jira";
	}

	@Override
	public String get(final DataFetchingEnvironment environment) throws Exception {
		log.info("calling jira data fetcher");
		final EvaluationDAO evaluationDAO = environment.getSource();
		final Optional<JiraUUIDDAO> jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(evaluationDAO.getId());
		return jiraUUIDDAO.orElse(JiraUUIDDAO.builder().build()).getJira();
	}
}
