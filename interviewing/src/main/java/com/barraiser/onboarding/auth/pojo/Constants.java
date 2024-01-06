/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.pojo;

public class Constants {
	public static final String CUSTOM_AUTH_CHALLENGE = "CUSTOM_CHALLENGE";

	public final static String REGION = "ap-south-1";

	// Cognito claims keys
	public static final String COGNITO_CLAIMS_KEY_PHONE_NUMBER = "phone_number";
	public static final String COGNITO_CLAIMS_KEY_PHONE_NUMBER_VERIFIED = "phone_number_verified";
	public static final String COGNITO_CLAIMS_KEY_EMAIL = "email";
	public static final String COGNITO_CLAIMS_KEY_EMAIL_VERIFIED = "email_verified";
	public static final String COGNITO_CLAIMS_KEY_USERNAME = "cognito:username";
	public static final String COGNITO_GROUPS = "cognito:groups";
	public static final String COGNITO_CLAIMS_KEY_CUSTOM_PARTNER_ID = "custom:partnerId";
	public static final Integer LAST_MINUTE_INTERVIEW_CANCELLATION_THRESHOLD = 1800;
}
