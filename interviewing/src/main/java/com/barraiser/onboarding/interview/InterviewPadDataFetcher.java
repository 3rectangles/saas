/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.dal.InterviewPadDAO;
import com.barraiser.onboarding.dal.InterviewPadRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.interview.interviewPad.InterviewPadAuthorizer;
import com.barraiser.onboarding.interviewing.interviewpad.InterviewPad;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InterviewPadDataFetcher extends AuthorizedGraphQLQuery_deprecated<InterviewPad> {
	private final InterviewPadRepository interviewPadRepository;
	private final String ENTITY = "Interview";
	private final String EXCEPTION_MESSAGE = "User does not have permission to view the details";

	public InterviewPadDataFetcher(final InterviewPadAuthorizer interviewPadAuthorizer,
			final ObjectFieldsFilter<InterviewPad> objectFieldsFilter,
			final InterviewPadRepository interviewPadRepository) {
		super(interviewPadAuthorizer, objectFieldsFilter);
		this.interviewPadRepository = interviewPadRepository;
	}

	@Override
	public InterviewPad fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals(this.ENTITY)) {
			final Interview interview = environment.getSource();
			final Optional<InterviewPadDAO> interviewPad = this.interviewPadRepository
					.findByInterviewId(interview.getId());

			if (interviewPad.isPresent()) {
				return InterviewPad.builder().interviewerPad(interviewPad.get().getInterviewerPad())
						.intervieweePad(interviewPad.get().getIntervieweePad())
						.build();
			}
			return InterviewPad.builder().build();
		}
		throw new AuthorizationException(this.EXCEPTION_MESSAGE);
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(this.ENTITY, "interviewPad"));
	}
}
