/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.input.AddCalendarInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.enums.OAuthProvider;
import com.barraiser.onboarding.availability.DTO.AddCalendarDTO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import static com.barraiser.onboarding.common.Constants.SAAS_TRIAL_PARTNERSHIP_MODEL_ID;

@Log4j2
@Component
@AllArgsConstructor
public class AddCalendarMutation implements GraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final AvailabilityServiceClient availabilityServiceClient;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public String name() {
		return "addCalendar";
	}

	@Override
	public Boolean get(DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final AddCalendarInput input = this.graphQLUtil.getInput(environment, AddCalendarInput.class);

		Integer noOfDaysForEvents = null;
		// Todo: can be reconfigurable in future
		if (input.getPartnerId() != null
				&& this.partnerCompanyRepository.findById(input.getPartnerId()).get().getPartnershipModelId()
						.equals(SAAS_TRIAL_PARTNERSHIP_MODEL_ID)) {
			noOfDaysForEvents = 30;
		}

		this.availabilityServiceClient.addCalendar(user.getUserName(), AddCalendarDTO.builder()
				.code(input.getCode())
				.redirectUri(input.getRedirectUri())
				.oAuthProvider(OAuthProvider.fromString(input.getOAuthProvider()))
				.noOfDaysForEvents(noOfDaysForEvents)
				.context(input.getContext())
				.build());
		return true;
	}
}
