/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/** Properties that are in dynamodb */
@Component
@Data
@AllArgsConstructor
@Log4j2
public class DynamicAppConfigProperties {
	private final Map<String, String> appConfigs;
	private final DynamoDBMapper dynamoDBMapper;
	private final ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		final PaginatedScanList<AppConfig> result = this.dynamoDBMapper.scan(AppConfig.class,
				new DynamoDBScanExpression());
		result.forEach(x -> this.appConfigs.put(x.getName(), x.getValue()));
	}

	public String getString(final String name) {
		return this.appConfigs.get(name);
	}

	public int getInt(final String name) {
		return Integer.parseInt(this.appConfigs.get(name));
	}

	public boolean getBoolean(final String name) {
		return Boolean.parseBoolean(this.appConfigs.get(name));
	}

	public Double getDouble(final String name) {
		return Double.parseDouble(this.appConfigs.get(name));
	}

	public List<String> getListOfString(final String name) {
		return List.of(this.appConfigs.get(name).split(","));
	}

	@Data
	@DynamoDBTable(tableName = "app-config")
	public static class AppConfig {
		@DynamoDBHashKey
		private String name;
		private String value;
	}

	public <T> T getObject(final String name, final Class<T> type) throws JsonProcessingException {
		return this.objectMapper.readValue(this.getString(name), type);
	}

	public <T> T getObjectFromJson(String json, TypeReference<T> typeReference) {
		try {
			return objectMapper.readValue(json, typeReference);
		} catch (Exception e) {
			log.error(e, e);
			return null;
		}
	}
}
