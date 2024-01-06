/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.whatsapp.consent;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.communication.automation.dal.WhatsAppConsentDAO;
import com.barraiser.communication.automation.dal.WhatsAppConsentRepository;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateWhatsappConsent implements GraphQLMutation<WhatsappConsent> {
	private final WhatsAppConsentRepository whatsAppConsentRepository;
	private final GraphQLUtil graphQLUtil;
	private final PhoneParser phoneParser;

	@Override
	public String name() {
		return "updateWhatsAppConsent";
	}

	@Override
	public WhatsappConsent get(final DataFetchingEnvironment environment) throws Exception {
		final UpdateWhatsappConsentInput input = this.graphQLUtil.getInput(environment,
				UpdateWhatsappConsentInput.class);

		final String phone = this.phoneParser.getFormattedPhone(input.getPhone());
		log.info(phone);

		final WhatsAppConsentDAO consent = this.whatsAppConsentRepository
				.findByPhone(phone)
				.orElse(
						WhatsAppConsentDAO.builder()
								.id(UUID.randomUUID().toString())
								.phone(phone)
								.build());

		this.whatsAppConsentRepository.save(
				consent.toBuilder().consent(input.getConsent()).build());

		return WhatsappConsent.builder()
				.phone(input.getPhone())
				.consent(input.getConsent())
				.submitted(true)
				.build();
	}
}
