/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.buttercms.ButterCMSClient;
import com.buttercms.IButterCMSClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.launchdarkly.sdk.server.LDClient;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;

import feign.Logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.catalina.Context;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.json.JSONObject;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "cors.origins")
@RequiredArgsConstructor
public class AppConfig {
	private static final int THREADS_COUNT = 20;
	private static final String DEFAULT_THREAD_POOL_PREFIX = "default_task_executor_thread";

	@Primary
	@Bean(name = "applicationEnvironemnt")
	public String applicationEnvironment(final Environment environment) {
		return environment.getActiveProfiles()[0];
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(THREADS_COUNT);
		threadPoolTaskScheduler.setThreadNamePrefix(DEFAULT_THREAD_POOL_PREFIX);
		return threadPoolTaskScheduler;
	}

	@Bean
	public PropertyUtilsBean propertyUtilsBean() {
		return new PropertyUtilsBean();
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();

		// For time modules of jackson
		objectMapper.findAndRegisterModules();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		return objectMapper;
	}

	@Bean
	public Validator getValidator() {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		return factory.getValidator();
	}

	@Bean
	PhoneNumberUtil getPhoneNumberUtil() {
		return PhoneNumberUtil.getInstance();
	}

	@Bean
	public HttpTraceRepository getHttpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
		return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {

			@Override
			public void customize(final TomcatServletWebServerFactory factory) {
				factory.addContextCustomizers(
						new TomcatContextCustomizer() {
							@Override
							public void customize(final Context context) {
								context.setCookieProcessor(new LegacyCookieProcessor());
							}
						});
			}
		};
	}

	@Bean
	public MessageBirdClient getMessageBirdClient(final AWSSecretsManager awsSecretsManager) {
		final String messageBirdApiKey = awsSecretsManager
				.getSecretValue(
						new GetSecretValueRequest().withSecretId("live/MessageBirdApiKey"))
				.getSecretString();
		final MessageBirdService messageBirdService = new MessageBirdServiceImpl(messageBirdApiKey);

		return new MessageBirdClient(messageBirdService);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource(
			final StaticAppConfigValues staticAppConfigValues,
			final AWSSecretsManager awsSecretsManager,
			final ObjectMapper objectMapper)
			throws Exception {

		final String secretJson = this.getConfigurations(awsSecretsManager, staticAppConfigValues);

		DatabaseSecret databaseSecret = null;
		try {
			databaseSecret = objectMapper.readValue(secretJson, DatabaseSecret.class);
		} catch (final JsonProcessingException e) {
			throw new Exception("Error extracting Database credentials from secret");
		}

		return DataSourceBuilder.create()
				.password(databaseSecret.getPassword())
				.username(databaseSecret.getUsername())
				.build();
	}

	@Autowired
	private Environment environment;

	private String getConfigurations(final AWSSecretsManager awsSecretsManager,
			final StaticAppConfigValues staticAppConfigValues) throws IOException {
		if (Arrays.asList(this.environment.getActiveProfiles()).contains("local")
				|| Arrays.asList(this.environment.getActiveProfiles()).contains("gitpod")) {
			final InputStream inputStream = new FileInputStream("local-db.conf");
			final Properties credentials = new Properties();
			credentials.load(inputStream);

			final JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", credentials.getProperty("USER_NAME"));
			jsonObject.put("password", credentials.getProperty("PASS_WORD"));
			jsonObject.put("engine", credentials.getProperty("ENGINE"));
			jsonObject.put("host", credentials.getProperty("HOST"));
			jsonObject.put("port", credentials.getProperty("PORT"));
			jsonObject.put("dbInstanceIdentifier", credentials.getProperty("DB_INSTANCE_IDENTIFIER"));

			return jsonObject.toString();
		} else {
			return awsSecretsManager
					.getSecretValue(
							new GetSecretValueRequest()
									.withSecretId(
											staticAppConfigValues.getDbPasswordSecretKey()))
					.getSecretString();
		}
	}

	@Bean
	public IButterCMSClient butterCMSClient(final StaticAppConfigValues staticAppConfigValues) {
		return new ButterCMSClient(staticAppConfigValues.getButterCmsKey());
	}

	@Bean
	public LDClient launchDarklyClient(
			final StaticAppConfigValues staticAppConfigValues,
			final AWSSecretsManager awsSecretsManager) {
		final String sdkKeySecretString = awsSecretsManager
				.getSecretValue(
						new GetSecretValueRequest()
								.withSecretId(
										staticAppConfigValues.getLaunchDarklySdkKeyName()))
				.getSecretString();
		return new LDClient(sdkKeySecretString);
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}

@Getter
class DatabaseSecret {
	private String username;
	private String password;
	private String engine;
	private String host;
	private String port;
	private String dbInstanceIdentifier;
}
