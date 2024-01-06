/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.config;

import com.barraiser.ats_integrations.dal.PartnerConfigurationDAO;
import com.barraiser.ats_integrations.dal.ATSPartnerConfigurationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Component
public class ATSPartnerConfigurationProvider {

	private final ObjectMapper objectMapper;

	@Qualifier("atsPartnerConfigurationRepository")
	private final ATSPartnerConfigurationRepository partnerConfigurationRepository;

	public <T> T getConfiguration(final String partnerId, final String module, final Class<T> mapperClass) {
		final JsonNode config = this.getPartnerConfiguration(partnerId).get(module);
		return this.objectMapper.convertValue(config, mapperClass);
	}

	private JsonNode getPartnerConfiguration(final String partnerId) {
		final Optional<PartnerConfigurationDAO> partnerConfigurationDAO = this.partnerConfigurationRepository
				.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId);

		if (!partnerConfigurationDAO.isPresent()) {
			throw new IllegalArgumentException("Partner Configuration is not present");
		}

		return partnerConfigurationDAO.get().getConfig();
	}

	/**
	 * Get the configuration of a module for all partners
	 *
	 * @param module
	 * @param mapperClass
	 * @param <T>
	 * @return
	 */
	public <T> Map<String, T> getCustomerConfigurations(final String module, final Class<T> mapperClass) {

		Map<String, T> partnerIdToConfigurationMapping = new HashMap<>();

		this.partnerConfigurationRepository.findAllByDeletedOnIsNull()
				.stream()
				.forEach(
						x -> {
							T moduleConfig = null;

							if (x.getConfig().has(module)) {
								moduleConfig = this.objectMapper.convertValue(x.getConfig().get(module), mapperClass);
							}

							if (moduleConfig != null) {
								partnerIdToConfigurationMapping.put(x.getPartnerId(), moduleConfig);
							}

						});

		return partnerIdToConfigurationMapping;
	}

}
