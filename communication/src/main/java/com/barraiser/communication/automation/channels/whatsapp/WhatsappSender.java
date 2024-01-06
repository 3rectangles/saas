/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp;

import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappRecipient;
import com.barraiser.communication.automation.channels.whatsapp.exceptions.BRMessageBirdException;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.dal.WhatsAppConsentDAO;
import com.barraiser.communication.automation.dal.WhatsAppConsentRepository;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappData;
import com.barraiser.communication.automation.pipeline.exception.SkipCommunicationException;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.conversations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class WhatsappSender implements CommunicationProcessor<WhatsappData> {
	public static final int NUMBER_OF_RETRIES = 3;

	private final WhatsAppConsentRepository whatsAppConsentRepository;
	private final MessageBirdClient messageBirdClient;
	private final CommunicationStaticAppConfig staticAppConfig;

	@Override
	public Channel getChannel() {
		return Channel.WHATSAPP;
	}

	@Override
	public void process(WhatsappData data) {

		if (!this.hasRecepient(data.getWhatsappRecipient())) {
			log.info("NO_RECEPIENT:Not sending whatsapp message as no recepient number available.");
			return;
		}

		String toWhatsappNumber = data.getWhatsappRecipient().getToPhoneNumber();
		final Optional<WhatsAppConsentDAO> consent = this.whatsAppConsentRepository.findByPhone(toWhatsappNumber);
		if (!RecipientType.CANDIDATE.equals(data.getInput().getRecipientType())
				|| (consent.isPresent() && consent.get().getConsent())) {
			final ConversationContent conversationContent = new ConversationContent();
			List<String> templateVariables = data.getWhatsappMessage().getTemplateVariables();
			final ConversationContentHsm hsm = new ConversationContentHsm(
					this.staticAppConfig.getMessageBirdWhatsappNamespace(),
					data.getWhatsappMessage().getTemplateName(),
					new ConversationHsmLanguage("en", ConversationHsmLanguagePolicy.DETERMINISTIC),
					templateVariables.stream().map(ConversationHsmLocalizableParameter::defaultValue)
							.collect(Collectors.toList()));

			conversationContent.setHsm(hsm);

			final ConversationStartRequest request = new ConversationStartRequest(
					toWhatsappNumber,
					ConversationContentType.HSM,
					conversationContent, this.getMessageBirdWhatsappChannelId(data.getInput().getRecipientType()));

			// added retries to handle cases where message bird is unable to send message
			// because of some issue at their end.
			// To be removed when we add retries at the entire communication module.
			for (int i = 0; i < NUMBER_OF_RETRIES; i++) {
				try {
					this.messageBirdClient.startConversation(request);
					break;
				} catch (final UnauthorizedException | GeneralException e) {
					log.error("Whatsapp conversation attempt failed : ", e);
					if (i == (NUMBER_OF_RETRIES - 1)) {
						throw new BRMessageBirdException(e.getMessage());
					}
				}
			}
		} else {
			throw new SkipCommunicationException("Whatsapp consent is not available : " + toWhatsappNumber);
		}

	}

	private String getMessageBirdWhatsappChannelId(final RecipientType recipientType) {
		if (RecipientType.EXPERT.equals(recipientType)) {
			return this.staticAppConfig.getMessageBirdExpertWhatsappChannelId();
		}
		return this.staticAppConfig.getMessageBirdCandidateWhatsappChannelId();
	}

	private Boolean hasRecepient(final WhatsappRecipient whatsappRecipient) {
		return whatsappRecipient.getToPhoneNumber() != null;
	}
}
