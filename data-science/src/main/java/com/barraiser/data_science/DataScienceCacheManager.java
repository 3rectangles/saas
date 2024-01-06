/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.barraiser.data_science.common.DataScienceStaticAppConfig;
import com.barraiser.data_science.dal.DataScienceCacheDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
@AllArgsConstructor
public class DataScienceCacheManager {
	private final DynamoDBMapper dynamoDBMapper;
	private final ObjectMapper objectMapper;

	public <T> T getResponseFromCache(final Object requestBody, Class<T> tClass) throws Exception {
		final String responseString = this.getResponseFromCache(requestBody);

		if (responseString == null) {
			return null;
		}

		return this.objectMapper
				.readValue(
						responseString,
						tClass);
	}

	private String getResponseFromCache(final Object requestBody) throws Exception {
		final String requestHash = this.getRequestHash(requestBody);

		final DataScienceCacheDAO dataScienceCacheDAO = this.dynamoDBMapper
				.load(DataScienceCacheDAO.class, requestHash);

		if (dataScienceCacheDAO != null) {
			return dataScienceCacheDAO.getResponseBody();
		}

		return null;
	}

	public void addResponseToCache(
			final Object requestBody,
			final Object responseBody)
			throws Exception {
		final String requestHash = this.getRequestHash(requestBody);

		final DataScienceCacheDAO dataScienceCacheDAO = new DataScienceCacheDAO();
		dataScienceCacheDAO
				.setRequestHash(requestHash);

		dataScienceCacheDAO
				.setRequestBody(this.objectMapper
						.writeValueAsString(requestBody));

		dataScienceCacheDAO
				.setResponseBody(this.objectMapper
						.writeValueAsString(responseBody));

		this.dynamoDBMapper
				.save(dataScienceCacheDAO);
	}

	private String getRequestHash(final Object requestBody) throws Exception {
		final byte[] requestBodyInBytes = this.getByteArray(requestBody);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] messageDigest = md.digest(requestBodyInBytes);

			BigInteger no = new BigInteger(1, messageDigest);

			StringBuilder hash = new StringBuilder(no.toString(16));
			while (hash.length() < 32) {
				hash.insert(0, "0");
			}
			return hash.toString();
		} catch (final NoSuchAlgorithmException e) {
			log.error("Unable to generate MD5 hash : ", e);
			throw new RuntimeException(e);
		}
	}

	private byte[] getByteArray(final Object object) throws Exception {
		return this.objectMapper.writeValueAsBytes(object);
	}
}
