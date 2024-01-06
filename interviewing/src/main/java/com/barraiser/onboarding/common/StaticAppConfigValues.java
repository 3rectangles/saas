/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Properties that are in application.yaml file
 */
@Component
@Getter
public class StaticAppConfigValues {

	@Value("${br-config.s3-bucket}")
	private String barraiserConfigS3Bucket;

	@Value("${bgs.algorithm.version}")
	private String currentEvaluationScoringAlgoVersion;

	@Value("${notification.interview.email}")
	private String interviewNotificationEmail;

	@Value("${queue.feedback}")
	private String feedbackSubmittedEventQueueUrl;

	@Value("${queue.expert-payment-calculation}")
	private String expertPaymentCalculationEventQueueUrl;

	@Value("${monitoring.enabled}")
	private String monitoringEnabled;

	@Value("${monitoring.slack-channel}")
	private String monitoringSlackChannel;

	@Value("${razorpay.secretNames.apiKeyId}")
	private String razorPayApiKeyId;

	@Value("${razorpay.secretNames.secretName}")
	private String razorPaySecretKey;

	@Value("${queue.jira}")
	private String jiraEventSQSUrl;

	@Value("${linkedinShare.redirectUrl}")
	private String redirectUri;

	@Value("${payment.redirection.host}")
	private String paymentRedirectionHost;

	@Value("${otp.message.phone}")
	private String loginOtpMessage;

	@Value("${interview.lifecycle.management.enabled}")
	private String interviewLifecycleManagementEnabled;

	@Value("${interview.ta-auto-allocation}")
	private String taAutoAllocationEnabled;

	@Value("${aws.secretNames.barraiserDb}")
	private String dbPasswordSecretKey;

	@Value("${queue.email-event}")
	private String emailEventSQSUrl;

	@Value("${redirect_url.url}")
	private String redirectURL;

	@Value("${notification.interview.lifecycle.email}")
	private String interviewLifecycleInformationEmail;

	@Value("${buttercms.key}")
	private String butterCmsKey;

	@Value("${aws.cognito.userPoolId}")
	private String userPoolId;

	@Value("${aws.cognito.backendClientId}")
	private String cognitoBackendClientId;

	@Value("${aws.cognito.backendClientSecretId}")
	private String cognitoBackendClientSecretId;

	@Value("${aws.cognito.expectedIssuerCognito}")
	private String expectedIssuerCognito;

	@Value("${aws.cognito.jwksDownloadUrl}")
	private String jwksDownloadUrl;

	@Value("${server.servlet.session.cookie.domain}")
	private String cookieDomain;

	@Value("${aws.eventBus}")
	private String eventBus;

	@Value("${queue.db-log-events-consumer}")
	private String dbLogEventsQueue;

	@Value("${queue.interviewing-events-consumer}")
	private String interviewingEventsConsumer;

	@Value("${launchdarkly.secretNames.sdk-key}")
	private String launchDarklySdkKeyName;

	@Value("${firebase.credentials-secret-name}")
	private String firebaseCredentialsSecretName;

	@Value("${firebase.database-url}")
	private String firebaseDatabaseUrl;

	@Value("${communication.phone.countryCode.india}")
	private Integer phoneCountryCodeIndia;

	@Value("${communication.phone.length.india}")
	private Integer phoneLengthIndia;

	@Value("${recaptcha-secret-key}")
	private String recaptchaSecretKey;

}
