/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.EmailHandler;
import com.barraiser.onboarding.communication.channels.email.EmailEvent;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barraiser.common.constants.Constants.INTERVIEW_COMMUNICATION_MASTER_EMAIL;

@Log4j2
@Component
@AllArgsConstructor
public class TaAvailabilityCommunicationService implements EmailHandler {
	private final StaticAppConfigValues staticAppConfigValues;
	private final EmailService emailService;
	private final DynamicAppConfigProperties appConfigProperties;
	public static final String TEMPLATE_TA_AVAILABILITY_MAIL_TO_OPS = "template_ta_availability_to_ops";

	public static final String BUFFER_FOR_REPORTING_TA_SHORTAGE = "buffer_for_reporting_ta_shortage";
	public static final String TIME_RANGE_FOR_REPORTING_SHORTAGE = "time_range_for_reporting_shortage";

	private final InterviewManager interviewManager;

	@Override
	public String subject() {
		return "TA shortage";
	}

	@Override
	public String objective() {
		return "send-ta-shortage-summary-email-to-ops-prod";
	}

	@Override
	public void process(EmailEvent emailEvent) {
		log.info("Started processing {} ", objective());
		final Long startTimeStamp = (System.currentTimeMillis() / 1000L)
				+ (long) this.appConfigProperties.getInt(TIME_RANGE_FOR_REPORTING_SHORTAGE);
		final Long endTimeStamp = startTimeStamp
				+ (long) this.appConfigProperties.getInt(BUFFER_FOR_REPORTING_TA_SHORTAGE);
		Map<String, Integer> taShortagePerSlot = interviewManager.getTaShortagePerSlot(startTimeStamp, endTimeStamp);
		if (taShortagePerSlot.size() == 0)
			return;
		this.sendMailToHosting(taShortagePerSlot, INTERVIEW_COMMUNICATION_MASTER_EMAIL);
	}

	private Map<String, String> constructMailData(final Map<String, Integer> taShortagePerSlot) {
		final Map<String, String> data = new HashMap<>();
		final StringBuilder stringBuilder = new StringBuilder();
		int counter = 1;
		for (String slot : taShortagePerSlot.keySet()) {
			stringBuilder.append(counter++).append(". ").append(slot).append(" - ").append(taShortagePerSlot.get(slot))
					.append(" TA required.").append("<br>");
		}
		data.put("slotData", stringBuilder.toString());
		return data;
	}

	private void sendMailToHosting(final Map<String, Integer> taShortagePerSlot, final String InterviewBarraiserEmail) {
		final Map<String, String> emailData = this.constructMailData(taShortagePerSlot);
		final String template = TEMPLATE_TA_AVAILABILITY_MAIL_TO_OPS;
		final List<String> toEmail = new ArrayList<>();
		toEmail.add(InterviewBarraiserEmail);
		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add("shivam.sharma@barraiser.com");

		try {
			this.emailService.sendEmail(InterviewBarraiserEmail, this.subject(), template, toEmail,
					ccEmail, emailData, null);
		} catch (final Exception e) {
			log.error(String.format("Error sending TA Shortage Mail to Hosting: due to {}", e));
		}
	}

}
