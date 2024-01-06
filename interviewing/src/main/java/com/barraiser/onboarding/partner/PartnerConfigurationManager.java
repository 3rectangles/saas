/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.config.PartnerConfigContextConstructor;
import com.barraiser.onboarding.dal.PartnerConfigurationDAO;
import com.barraiser.onboarding.dal.PartnerConfigurationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//TODO: This class will be cleaned up once all the config from partner_configurations table is moved to s3 config.
@Component
@AllArgsConstructor
@Log4j2
public class PartnerConfigurationManager {
	private static final String INTERVIEW_SCHEDULING_KEY = "interviewScheduling";
	private static final String IS_CANDIDATE_SCHEDULING_ENABLED_KEY = "isCandidateSchedulingEnabled";
	private static final String IS_EVALUATION_RECOMMENDATION_ENABLED = "recommendation";
	private static final String BGS = "bgs";

	// Old repo where configs were kept till a point
	private final PartnerConfigurationRepository partnerConfigurationRepository;
	private final ObjectMapper objectMapper;

	// New source of getting config
	private final PartnerConfigContextConstructor partnerConfigContextConstructor;
	private final ConfigComposer configComposer;

	/**
	 * NOTE : This method when called adds default tags based on
	 * partnership_model,user_role by default.
	 */
	public JsonNode getPartnerConfiguration(final String tag, final String partnerId,
			final Map<String, Object> configInput, final AuthenticatedUser authenticatedUser) throws IOException {

		List<String> contextTags = this.partnerConfigContextConstructor.getContextTags(partnerId, configInput,
				authenticatedUser);
		return this.configComposer.compose(tag, contextTags);
	}

	private JsonNode getPartnerConfiguration(final String partnerId) {
		final Optional<PartnerConfigurationDAO> partnerConfigurationDAO = this.partnerConfigurationRepository
				.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId);

		if (!partnerConfigurationDAO.isPresent()) {
			throw new IllegalArgumentException("Partner Configuration is not present");
		}

		return partnerConfigurationDAO.get().getConfig();
	}

	public Boolean isCandidateSchedulingEnabled(final String partnerId) {
		final JsonNode config = this.getPartnerConfiguration(partnerId);

		final JsonNode candidateSchedulingConfiguration = config.get(INTERVIEW_SCHEDULING_KEY)
				.get(IS_CANDIDATE_SCHEDULING_ENABLED_KEY);

		if (candidateSchedulingConfiguration == null || candidateSchedulingConfiguration.isNull()) {
			return true;
		}

		return candidateSchedulingConfiguration.asBoolean();
	}

	public Boolean isRecommendationEnabled(final String partnerId) {
		final JsonNode config = this.getPartnerConfiguration(partnerId);
		JsonNode recommendationEnabled = null;
		if (config.get(BGS) != null) {
			recommendationEnabled = config.get(BGS)
					.get(IS_EVALUATION_RECOMMENDATION_ENABLED);
		}
		if (recommendationEnabled == null || recommendationEnabled.isNull()) {
			return true;
		}
		return recommendationEnabled.asBoolean();
	}

	public <T> T getConfiguration(final String partnerId, final String module, final Class<T> mapperClass) {
		final JsonNode config = this.getPartnerConfiguration(partnerId).get(module);
		return this.objectMapper.convertValue(config, mapperClass);
	}
}
