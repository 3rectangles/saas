/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms;

import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.sms.dto.SmsData;
import com.barraiser.communication.automation.channels.sms.dto.SmsMessage;
import com.barraiser.communication.automation.template.TemplatePopulator;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.SmsTemplateDAO;
import com.barraiser.communication.automation.dal.SmsTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class SmsTemplatePopulator implements CommunicationProcessor<SmsData> {

	private final SmsTemplateRepository smsTemplateRepository;
	private final TemplatePopulator templatePopulator;
	private final QueryDataFetcher queryDataFetcher;

	@Override
	public Channel getChannel() {
		return Channel.SMS;
	}

	@Override
	public void process(SmsData data) {
		final SmsTemplateDAO smsTemplateDAO = this.smsTemplateRepository.findById(data.getInput().getTemplateId())
				.get();
		final Object queryData = this.queryDataFetcher.fetchQueryData(smsTemplateDAO.getQuery(),
				data.getInput().getEntity());
		final String bodyPopulated = this.templatePopulator.populateTemplate(smsTemplateDAO.getBody(), queryData);
		data.setSmsMessage(SmsMessage.builder()
				.body(bodyPopulated)
				.build());
	}
}
