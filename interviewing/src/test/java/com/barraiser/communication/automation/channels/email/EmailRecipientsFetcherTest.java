/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email;

import com.barraiser.communication.automation.channels.email.dto.EmailData;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.barraiser.communication.util.RecipientFetchingHelper;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EmailRecipientsFetcherTest {

	@Mock
	private CommunicationStaticAppConfig staticAppConfig;

	@Mock
	private RecipientFetchingHelper recipientFetchingHelper;

	@Mock
	EmailData data;

	@Test
	public void processDataWithNullToEmailsFromPayload() {
		EmailRecipient recipient = EmailRecipient.builder()
				.toEmails(List.of("mock@mock.com"))
				.build();
		List<String> toEmails = new ArrayList<>();
		toEmails.addAll(recipient.getToEmails());

		Assert.notEmpty(recipient.getToEmails());
	}

	@Test(expected = IllegalArgumentException.class)
	public void processDataWithNullToEmails() {
		EmailRecipient recipient = EmailRecipient.builder()
				.build();
		List<String> toEmails = new ArrayList<>();
		if (recipient.getToEmails() != null) {
			toEmails.addAll(recipient.getToEmails());
		}

		Assert.notEmpty(recipient.getToEmails());
	}

	@Test(expected = IllegalArgumentException.class)
	public void processDataWithNullCcEmails() {
		EmailRecipient recipient = EmailRecipient.builder()
				.build();
		List<String> ccEmails = new ArrayList<>();
		if (recipient.getCcEmails() != null) {
			ccEmails.addAll(recipient.getCcEmails());
		}

		Assert.notEmpty(recipient.getCcEmails());
	}

	@Test
	public void processDataWithCcEmails() {
		EmailRecipient recipient = EmailRecipient.builder()
				.ccEmails(List.of("Aasa"))
				.build();
		List<String> ccEmails = new ArrayList<>();
		if (recipient.getCcEmails() != null) {
			ccEmails.addAll(recipient.getCcEmails());
		}

		recipient.toBuilder()
				.ccEmails(ccEmails)
				.build();

		Assert.notEmpty(recipient.getCcEmails());
	}

}
