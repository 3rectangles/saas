/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class ConfigComposer {

	private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String localConfigPath = null; // = "/workspace/config/barraiser-config-management/configs/";
	private final StaticAppConfigValues staticAppConfigValues;

	/**
	 * @param tag
	 * @param contextTags
	 * @return
	 * @throws IOException
	 */
	public JsonNode compose(final String tag, final List<String> contextTags) throws IOException {

		// Load tag-specific config from local storage, if exists
		ObjectNode tagConfig;

		// If local config not found, load from S3
		if (this.localConfigPath != null) {
			tagConfig = this.loadJsonFile(this.localConfigPath + tag + ".json");
		} else {
			tagConfig = this.loadJsonFileFromS3(tag + ".json");
		}

		JsonNode defaultConfig = (JsonNode) tagConfig.get("default");

		// Merge context-specific configs in the order provided
		for (String contextTag : contextTags) {
			JsonNode contextConfig = tagConfig.get(contextTag);
			defaultConfig = this.mergeConfigs(defaultConfig, contextConfig);
		}

		return defaultConfig;
	}

	private ObjectNode loadJsonFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists() && !file.isDirectory()) {
			JsonNode fileContent = this.objectMapper.readTree(file);
			return (ObjectNode) fileContent;
		} else {
			return null;
		}
	}

	private ObjectNode loadJsonFileFromS3(String fileName) throws IOException {
		String fileContent = this.s3Client.getObjectAsString(this.staticAppConfigValues.getBarraiserConfigS3Bucket(),
				fileName);
		return (ObjectNode) this.objectMapper.readTree(fileContent);
	}

	private JsonNode mergeConfigs(JsonNode baseConfig, JsonNode overrideConfig) {
		if (overrideConfig == null) {
			return baseConfig;
		}
		ObjectNode mergedConfig = this.objectMapper.createObjectNode();
		baseConfig.fields().forEachRemaining(entry -> mergedConfig.set(entry.getKey(), entry.getValue()));
		overrideConfig.fields().forEachRemaining(entry -> {
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			if (mergedConfig.has(key)) {
				JsonNode defaultValue = mergedConfig.get(key);
				if (defaultValue.getNodeType() == JsonNodeType.OBJECT && value.getNodeType() == JsonNodeType.OBJECT) {
					mergedConfig.set(key, this.mergeConfigs(defaultValue, value));
				} else {
					mergedConfig.set(key, value);
				}
			} else {
				mergedConfig.set(key, value);
			}
		});
		return mergedConfig;
	}
}
