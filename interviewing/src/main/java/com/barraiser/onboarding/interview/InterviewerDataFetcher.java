/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.user.TimezoneManager;
import com.barraiser.onboarding.user.auth.UserDetailsAuthorizer;
import com.barraiser.common.graphql.UserDetailsInput;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewerDataFetcher implements MultiParentTypeDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ExpertRepository expertRepository;
	private final Authorizer authorizer;
	private final UserDetailsRepository userDetailsRepository;
	private final TimezoneManager timezoneManager;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getExpertDetails"),
				List.of(Constants.TYPE_INTERVIEW, "interviewer"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals(Constants.TYPE_INTERVIEW)) {
			final Interview source = environment.getSource();
			if (source.getInterviewerId() != null) {
				final UserDetailsDAO userDetailsDAO = this.userDetailsRepository.findById(source.getInterviewerId())
						.get();
				return DataFetcherResult.newResult()
						.data(this.getExpertDetails(userDetailsDAO))
						.localContext(userDetailsDAO)
						.build();
			}
			return DataFetcherResult.newResult().build();
		} else if (type.getName().equals(QUERY_TYPE)) {
			if (type.getName().equals(QUERY_TYPE)) {
				final UserDetailsInput input = this.graphQLUtil.getInput(environment, UserDetailsInput.class);
				final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
				final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
						.type(UserDetailsAuthorizer.RESOURCE_TYPE)
						.resource(input.getId() == null ? "" : input.getId())
						.build();
				this.authorizer.can(
						authenticatedUser,
						UserDetailsAuthorizer.ACTION_READ_EXPERT_DETAILS,
						authorizationResource);
				final UserDetailsDAO userDetailsDAO = this.userDetailsRepository.findById(input.getId()).get();
				return DataFetcherResult.newResult()
						.data(this.getExpertDetails(userDetailsDAO))
						.localContext(userDetailsDAO)
						.build();
			}
			throw new IllegalArgumentException();
		}
		throw new IllegalArgumentException();
	}

	private Interviewer getExpertDetails(final UserDetailsDAO userDetailsDAO) {
		final ExpertDAO expertDAO = this.expertRepository
				.findById(userDetailsDAO.getId())
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Either the user is not an expert or the expert is"
										+ " not yet approved"));

		return Interviewer.builder()
				.id(userDetailsDAO.getId())
				.initials(userDetailsDAO.getInitials())
				.almaMater(userDetailsDAO.getAlmaMater())
				.designation(userDetailsDAO.getDesignation())
				.workExperienceInMonths(userDetailsDAO.getWorkExperienceInMonths())
				.bankAccount(expertDAO.getBankAccount())
				.pan(expertDAO.getPan())
				.offerLetter(expertDAO.getOfferLetter())
				.expertDomains(userDetailsDAO.getExpertDomains())
				.peerDomains(userDetailsDAO.getPeerDomains())
				.totalInterviewsCompleted(expertDAO.getTotalInterviewsCompleted())
				.tenantId(expertDAO.getTenantId())
				.timezone(this.timezoneManager.getTimezoneOfExpert(userDetailsDAO.getId()))
				.minPrice(Money.builder().value(expertDAO.getMinPrice()).currency(expertDAO.getCurrency()).build())
				.build();

	}
}
