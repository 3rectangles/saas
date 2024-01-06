/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.whatsapp.consent;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.communication.automation.dal.WhatsAppConsentDAO;
import com.barraiser.communication.automation.dal.WhatsAppConsentRepository;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class GetWhatsAppConsent implements GraphQLQuery<WhatsappConsent> {
	private final WhatsAppConsentRepository whatsAppConsentRepository;
	private final GraphQLUtil graphQLUtil;
	private final PhoneParser phoneParser;

	@Override
	public String name() {
		return "getWhatsAppConsent";
	}

	@Override
	public WhatsappConsent get(final DataFetchingEnvironment environment) throws Exception {
		final GetWhatsappConsentInput input = this.graphQLUtil.getInput(environment, GetWhatsappConsentInput.class);

		final String phone = this.phoneParser.getFormattedPhone(input.getPhone());
		final Optional<WhatsAppConsentDAO> result = this.whatsAppConsentRepository.findByPhone(phone);

		final boolean consent = result.isPresent() && result.get().getConsent();

		return WhatsappConsent.builder()
				.submitted(result.isPresent())
				.phone(input.getPhone())
				.consent(consent)
				.build();
	}
}
