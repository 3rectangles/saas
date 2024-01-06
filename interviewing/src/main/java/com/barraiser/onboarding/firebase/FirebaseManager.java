/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.firebase;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.commons.auth.UserRole;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Log4j2
public class FirebaseManager {
	private final AWSSecretsManager awsSecretsManager;
	private final StaticAppConfigValues staticAppConfigValues;

	@PostConstruct
	private void initializeApp() throws IOException {
		final String serviceAccount = this.awsSecretsManager
				.getSecretValue(
						new GetSecretValueRequest()
								.withSecretId(
										staticAppConfigValues
												.getFirebaseCredentialsSecretName()))
				.getSecretString();
		final FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(
						GoogleCredentials.fromStream(
								new ByteArrayInputStream(serviceAccount.getBytes())))
				.setDatabaseUrl(staticAppConfigValues.getFirebaseDatabaseUrl())
				.build();
		FirebaseApp.initializeApp(options);
	}

	public String getFirebaseAuthToken(final AuthenticatedUser user) {
		try {
			final Map<String, Object> claims = new HashedMap();
			claims.put("barraiserUser", true);
			claims.put(
					"roles",
					user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toList()));
			return FirebaseAuth.getInstance().createCustomToken(user.getUserName(), claims);
		} catch (Exception e) {
			log.error(e, e);
			return null;
		}
	}

	public Firestore getFirestoreDb() {
		return FirestoreClient.getFirestore();
	}
}
