/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.ivr;

import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.ivr.dto.IvrData;
import com.barraiser.communication.automation.channels.ivr.dto.IvrMessage;
import com.barraiser.communication.automation.template.TemplatePopulator;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.IvrTemplateDAO;
import com.barraiser.communication.automation.dal.IvrTemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class IvrTemplatePopulator implements CommunicationProcessor<IvrData> {
	private final static String GET_COMPANY_NAME_QUERY = "query GetPartner($input: PartnerInput!) {\n" +
			"  getPartner(input: $input) {\n" +
			"    companyDetails {\n" +
			"      name\n" +
			"    }\n" +
			"  }\n" +
			"}";

	private final IvrTemplateRepository ivrTemplateRepository;
	private final TemplatePopulator templatePopulator;
	private final QueryDataFetcher queryDataFetcher;

	@Override
	public Channel getChannel() {
		return Channel.IVR;
	}

	@Override
	public void process(IvrData data) {
		final IvrTemplateDAO ivrTemplateDAO = this.ivrTemplateRepository.findById(data.getInput().getTemplateId())
				.get();

		final Object queryData = this.queryDataFetcher.fetchQueryData(ivrTemplateDAO.getQuery(),
				data.getInput().getEntity());
		final String bodyPopulated = this.templatePopulator.populateTemplate(ivrTemplateDAO.getBody(), queryData);
		final String subjectPopulated = this.templatePopulator.populateTemplate(ivrTemplateDAO.getIvrContextVariables(),
				queryData);

		Map<String, String> response = new HashMap<>();
		try {
			response = new ObjectMapper().readValue(subjectPopulated, HashMap.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		data.setIvrContextVariables(response);
		data.setIvrMessage(IvrMessage.builder()
				.body(bodyPopulated)
				.build());

	}
}
