/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.email.dto.EmailData;
import com.barraiser.communication.automation.channels.email.dto.EmailMessage;
import com.barraiser.communication.automation.template.TemplatePopulator;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.EmailTemplateDAO;
import com.barraiser.communication.automation.dal.EmailTemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class EmailTemplatePopulator implements CommunicationProcessor<EmailData> {
	private final static String GET_COMPANY_NAME_AND_LOGO_QUERY = "query GetPartner($input: PartnerInput!) {\n" +
			"  getPartner(input: $input) {\n" +
			"    companyDetails {\n" +
			"      name\n" +
			"      logo\n" +
			"    }\n" +
			"  }\n" +
			"}";
	private final static String BASE_TEMPLATE_PATH_PREFIX = "email_templates/";
	private final static String PARTNER_BRANDING = "PARTNER";
	private final static String BARRAISER_URL = "barraiser.com";
	private final static String PARTIAL_CONTENT = "{{#partial \"content\" }} %s {{/partial}}";

	private final Map<String, String> brandingToTemplateMap = Map.of(
			"BARRAISER", "barraiser_branded_template",
			PARTNER_BRANDING, "partner_branded_template");

	private final EmailTemplateRepository emailTemplateRepository;
	private final TemplatePopulator templatePopulator;
	private final QueryDataFetcher queryDataFetcher;

	@Override
	public Channel getChannel() {
		return null;
	}

	@Override
	public void process(EmailData data) {
		final EmailTemplateDAO emailTemplateDAO = this.emailTemplateRepository.findById(data.getInput().getTemplateId())
				.get();

		final Object queryData = this.queryDataFetcher.fetchQueryData(emailTemplateDAO.getQuery(),
				data.getInput().getEntity());

		((Map<String, Object>) queryData).put("partnerId", data.getInput().getEntity().getPartnerId());

		((Map<String, Object>) queryData).put("event", data.getInput().getEventPayload());

		final String templateBody = this.templatePopulator.populateTemplate(emailTemplateDAO.getBody(), queryData);

		final String emailTemplate = this.templatePopulator.getTemplateStringFromBaseTemplate(
				this.appendPartialBlocks(templateBody),
				this.getBaseTemplate(emailTemplateDAO.getBranding()));

		final Boolean shouldIncludePrivacyLink = this.shouldIncludePrivacyLink(templateBody,
				data.getInput().getRecipientType());
		((Map<String, Object>) queryData).put("shouldIncludePrivacyLink", shouldIncludePrivacyLink);

		final String subjectPopulated = this.templatePopulator.populateTemplate(emailTemplateDAO.getSubject(),
				queryData);
		final String companyNameAndLogo[] = this.getCompanyNameAndLogo(data.getInput().getEntity().getPartnerId());
		if (PARTNER_BRANDING.equals(emailTemplateDAO.getBranding())) {
			((Map<String, Object>) queryData).put("company_name",
					companyNameAndLogo[0]);
			((Map<String, Object>) queryData).put("company_logo",
					companyNameAndLogo[1]);
		}

		((Map<String, Object>) queryData).put("title", subjectPopulated);

		data.setMessage(EmailMessage.builder()
				.subject(subjectPopulated)
				.body(this.templatePopulator.populateTemplate(emailTemplate, queryData))
				.header(emailTemplateDAO.getHeader() != null
						? this.templatePopulator.populateTemplate(emailTemplateDAO.getHeader(), queryData)
						: "")
				.build());
	}

	private String getBaseTemplate(final String branding) {
		if (!this.brandingToTemplateMap.containsKey(branding)) {
			throw new IllegalArgumentException("invalid branding for email template : " + branding);
		}
		return BASE_TEMPLATE_PATH_PREFIX + this.brandingToTemplateMap.get(branding);
	}

	private String[] getCompanyNameAndLogo(final String partnerId) {
		final Object queryData = this.queryDataFetcher.fetchQueryData(
				GET_COMPANY_NAME_AND_LOGO_QUERY,
				Entity.builder()
						.type(EntityType.PARTNER)
						.id(partnerId)
						.build());
		final ObjectMapper objectMapper = new ObjectMapper();
		final Partner partner = objectMapper.convertValue(
				this.queryDataFetcher.getObjectFromPath(queryData, List.of("getPartner", "0")), Partner.class);
		return new String[] { partner.getCompanyDetails().getName(), partner.getCompanyDetails().getLogo() };
	}

	private Boolean shouldIncludePrivacyLink(final String body, final RecipientType recipientType) {
		if (body.contains(BARRAISER_URL) && RecipientType.CANDIDATE.equals(recipientType)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private String appendPartialBlocks(final String template) {
		return String.format(PARTIAL_CONTENT, template);
	}
}
