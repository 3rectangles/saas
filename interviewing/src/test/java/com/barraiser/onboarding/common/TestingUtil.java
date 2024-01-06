/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@AllArgsConstructor
public class TestingUtil {

	private final ObjectMapper objectMapper;

	public <T> T getTestingData(final String testDataFilePath, final Class<T> className) throws IOException {
		return objectMapper.readValue(this.getJsonFromFile(testDataFilePath), className);
	}

	public String getJsonFromFile(final String testDataFilePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(testDataFilePath)));
	}

}
