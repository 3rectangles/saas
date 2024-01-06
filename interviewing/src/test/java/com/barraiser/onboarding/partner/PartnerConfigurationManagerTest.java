/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.dal.PartnerConfigurationDAO;
import com.barraiser.onboarding.dal.PartnerConfigurationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerConfigurationManagerTest {
	@Mock
	private PartnerConfigurationRepository partnerConfigurationRepository;
	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private PartnerConfigurationManager partnerConfigurationManager;

	final String configWithRecommendation = "{\n" +
			"  \"bgs\": {\n" +
			"    \"recommendation\": true\n" +
			"  }\n" +
			"}";
	final String configWithoutRecommendation = "{\n" +
			"  \"bgs\": {\n" +
			"    \"recommendation\": false\n" +
			"  }\n" +
			"}";
	final String configWithoutRecommendationValue = "{\n" +
			"    \"interviewScheduling\": {\n" +
			"        \"isCandidateSchedulingEnabled\": false\n" +
			"    }\n" +
			"}";

	@Test
	public void testIfRecommendationIsEnabled() throws JsonProcessingException {
		// GIVEN
		final ObjectMapper tempObjectMapper = new ObjectMapper();
		final JsonNode jsonNode = tempObjectMapper.readValue(configWithRecommendation, JsonNode.class);

		final String partnerId = "a partner id";
		when(partnerConfigurationRepository.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId)).thenReturn(
				Optional.of(PartnerConfigurationDAO.builder()
						.partnerId(partnerId)
						.config(jsonNode)
						.build()));

		// WHEN
		final Boolean isRecommendationEnabled = partnerConfigurationManager.isRecommendationEnabled(partnerId);

		// THEN
		assertTrue(isRecommendationEnabled);
	}

	@Test
	public void testIfRecommendationIsNotEnabled() throws JsonProcessingException {
		// GIVEN
		final ObjectMapper tempObjectMapper = new ObjectMapper();
		final JsonNode jsonNode = tempObjectMapper.readValue(configWithoutRecommendation, JsonNode.class);

		final String partnerId = "a partner id";
		when(partnerConfigurationRepository.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId)).thenReturn(
				Optional.of(PartnerConfigurationDAO.builder()
						.partnerId(partnerId)
						.config(jsonNode)
						.build()));

		// WHEN
		final Boolean isRecommendationEnabled = partnerConfigurationManager.isRecommendationEnabled(partnerId);

		// THEN
		assertFalse(isRecommendationEnabled);
	}

	@Test
	public void shouldReturnTrueIfRecommendationValueNotPresent() throws JsonProcessingException {
		final ObjectMapper tempObjectMapper = new ObjectMapper();
		final JsonNode jsonNode = tempObjectMapper.readValue(configWithoutRecommendationValue, JsonNode.class);

		final String partnerId = "a partner id";
		when(partnerConfigurationRepository.findFirstByPartnerIdOrderByCreatedOnDesc(partnerId)).thenReturn(
				Optional.of(PartnerConfigurationDAO.builder()
						.partnerId(partnerId)
						.config(jsonNode)
						.build()));

		// WHEN
		final Boolean isRecommendationEnabled = partnerConfigurationManager.isRecommendationEnabled(partnerId);

		// THEN
		assertTrue(isRecommendationEnabled);
	}
}
