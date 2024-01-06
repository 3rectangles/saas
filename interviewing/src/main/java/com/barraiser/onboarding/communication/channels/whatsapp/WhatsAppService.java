/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.whatsapp;

import com.barraiser.communication.automation.dal.WhatsAppConsentDAO;
import com.barraiser.communication.automation.dal.WhatsAppConsentRepository;
import com.barraiser.communication.automation.channels.whatsapp.exceptions.UserConsentNotAvailableException;
import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.conversations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class WhatsAppService {
	public static final String WHATSAPP_NAMESPACE = "00225efe_0bbc_4fab_8456_7f1952833ec0";
	public static final String BARRAISER_WHATSAPP_CHANNEL_ID = "d8f31734-e26f-42d0-ab26-ed640ead41b4";

	private final WhatsAppConsentRepository whatsAppConsentRepository;
	private final MessageBirdClient messageBirdClient;

	/**
	 * Sends a templated message to the given contact. If the contact's consent is
	 * not available or not enabled, it will
	 * fail.
	 *
	 * @param phone
	 * @param templateName
	 */
	public void sendTemplateMessage(final String phone, final String templateName, final List<String> data)
			throws UnauthorizedException, GeneralException {
		final Optional<WhatsAppConsentDAO> consent = this.whatsAppConsentRepository.findByPhone(phone);
		if (consent.isPresent() && consent.get().getConsent()) {
			final ConversationContent conversationContent = new ConversationContent();
			final ConversationContentHsm hsm = new ConversationContentHsm(
					WHATSAPP_NAMESPACE,
					templateName,
					new ConversationHsmLanguage("en", ConversationHsmLanguagePolicy.DETERMINISTIC),
					data.stream().map(ConversationHsmLocalizableParameter::defaultValue).collect(Collectors.toList()));

			conversationContent.setHsm(hsm);
			final ConversationStartRequest request = new ConversationStartRequest(
					phone,
					ConversationContentType.HSM,
					conversationContent,
					BARRAISER_WHATSAPP_CHANNEL_ID);

			this.messageBirdClient.startConversation(request);
		} else {
			throw new UserConsentNotAvailableException("Whatsapp consent is not available : " + phone);
		}
	}
}
