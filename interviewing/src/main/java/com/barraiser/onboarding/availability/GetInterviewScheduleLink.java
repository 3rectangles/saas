/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.types.SchedulingInfo;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class GetInterviewScheduleLink implements NamedDataFetcher<DataFetcherResult<Object>> {
	private final GraphQLUtil graphQLUtil;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public String name() {
		return "getInterviewScheduleLink";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public DataFetcherResult<Object> get(final DataFetchingEnvironment environment)
			throws Exception {
		final String jiraId = this.graphQLUtil.getArgument(environment, "input", String.class);
		if (jiraId == null) {
			throw new IllegalArgumentException("jira id is not provided in the input");
		}
		final Optional<JiraUUIDDAO> jiraUUID = this.jiraUUIDRepository.findByJira(jiraId);
		final Optional<InterviewDAO> interview = this.interViewRepository.findById(jiraUUID.get().getUuid());
		final Optional<EvaluationDAO> evaluation = this.evaluationRepository
				.findById(interview.get().getEvaluationId());
		final Optional<PartnerCompanyDAO> partnerCompany = this.partnerCompanyRepository
				.findByCompanyId(evaluation.get().getCompanyId());
		if (!jiraUUID.isPresent()) {
			throw new IllegalArgumentException("Jira Id is invalid");
		} else {
			String interviewScheduleLink = "https://app.barraiser.com/scheduling?interview=" + jiraUUID.get().getUuid();
			String newSchedulingLink = "https://app.barraiser.com/partner/"
					+ partnerCompany.get().getId()
					+ "/evaluations?eid="
					+ interview.get().getEvaluationId()
					+ "&st=Candidate%20Delay&s=createdOn,desc&";
			return DataFetcherResult.newResult()
					.data(
							SchedulingInfo.builder()
									.oldLink(interviewScheduleLink)
									.newLink(newSchedulingLink)
									.build())
					.build();
		}
	}
}
