/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.magicLink;

import com.barraiser.common.graphql.input.MagicLinkInput;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class SendMagicLinkMutation implements GraphQLMutation<Boolean> {
	private final static Long MAGIC_LINK_EXPIRATION = 15 * 60L;
	private final MagicLinkManager magicLinkManager;
	private final EmailService emailService;
	private final PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	private final EvaluationManager evaluationManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return "sendMagicLink";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	/**
	 * Feature disabled from UI.
	 *
	 * @param environment
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean get(DataFetchingEnvironment environment) throws Exception {
		// final MagicLinkInput input =
		// this.objectMapper.convertValue(environment.getArgument("input"),
		// MagicLinkInput.class);
		// log.info("sending magic url for email : " + input.getEmail());
		// if (input.getEvaluationIdToSignUpPartnerFor() != null) {
		// this.signUpPartnerForBGS(input.getEmail(),
		// input.getEvaluationIdToSignUpPartnerFor());
		// }
		// final String magicUrl =
		// this.magicLinkManager.generateMagicUrl(input.getRedirectUrl(),
		// input.getEmail(),
		// MAGIC_LINK_EXPIRATION);
		// this.emailMagicLink(input.getEmail(), magicUrl);
		return Boolean.TRUE;
	}

	private void emailMagicLink(final String email, final String magicUrl) throws IOException {
		final Map<String, String> data = new HashMap<>();
		data.put("magic_link", magicUrl);
		this.emailService.sendEmail(email, "BarRaiser Login Link", "send_magic_link", data, null);
	}

	private void signUpPartnerForBGS(final String email, final String evaluationId) {
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationId);
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, partnerId);
	}
}
