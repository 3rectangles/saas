/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AwsClientConfig {
	@Bean
	public AmazonS3 getAmazonS3() {
		return AmazonS3ClientBuilder
				.standard()
				.enableForceGlobalBucketAccess()
				.build();
	}

	@Bean
	public TransferManager getS3TransferManager(final AmazonS3 s3Client) {
		return TransferManagerBuilder.standard()
				.withS3Client(s3Client)
				.withMultipartUploadThreshold((long) (5 * 1024 * 1025))
				.build();
	}

	@Bean
	public DynamoDBMapper getDynamoDBMapper(final Environment environment) {
		final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				.build();
		final String activeProfile = environment.getActiveProfiles()[0];
		final String tablePrefix = "prod".equals(activeProfile) ? activeProfile + "-" : "";
		final DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
				.withTableNameOverride(DynamoDBMapperConfig.TableNameOverride
						.withTableNamePrefix(tablePrefix))

				.build();
		return new DynamoDBMapper(client, config);
	}

	@Bean
	public AmazonSNS getAmazonSns() {
		return AmazonSNSClientBuilder.defaultClient();
	}

	@Bean
	public AWSCognitoIdentityProvider getAwsCognitoIdentityProvider() {
		return AWSCognitoIdentityProviderClientBuilder.standard().build();
	}

	@Bean
	public AWSSecretsManager getSecretManager() {
		return AWSSecretsManagerClientBuilder.standard().build();
	}

	@Bean
	public AmazonSimpleEmailService getAmazonSimpleEmailService() {
		return AmazonSimpleEmailServiceClientBuilder.standard().build();
	}

	@Bean
	public AmazonSQS amazonSQS() {
		return AmazonSQSClientBuilder.defaultClient();
	}

	@Bean
	public AWSStepFunctions awsStepFunctionsClient() {
		return AWSStepFunctionsClientBuilder.

				defaultClient();
	}

	@Bean
	public AmazonPinpoint getAmazonPinpointClient() {
		return AmazonPinpointClientBuilder.standard().build();
	}

	@Bean
	public AWSKMS getAWSKMSClient() {
		return AWSKMSClientBuilder.standard().build();
	}
}
