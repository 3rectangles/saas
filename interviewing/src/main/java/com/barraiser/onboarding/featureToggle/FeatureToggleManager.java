/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.featureToggle;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.launchdarkly.sdk.ArrayBuilder;
import com.launchdarkly.sdk.LDUser;
import com.launchdarkly.sdk.LDValue;
import com.launchdarkly.sdk.server.FeatureFlagsState;
import com.launchdarkly.sdk.server.LDClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class FeatureToggleManager {
	private final LDClient client;

	private LDUser buildLDUser(final AuthenticatedUser user) {
		if (user == null) {
			return new LDUser.Builder("anonymous")
					.anonymous(true)
					.build();
		}

		final ArrayBuilder ldRolesBuilder = LDValue.buildArray();
		user.getRoles().forEach(role -> ldRolesBuilder.add(role.getRole()));

		return new LDUser.Builder(user.getUserName())
				.email(user.getEmail())
				.custom("partnerId", user.getPartnerId())
				.custom("roles", ldRolesBuilder.build())
				.anonymous(false)
				.build();
	}

	public Map<String, Boolean> getFeatureTogglesForUser(final AuthenticatedUser user) {
		final LDUser ldUser = this.buildLDUser(user);
		return this.getFeatureToggleForLDUser(ldUser);
	}

	public Map<String, Boolean> getFeatureToggleForEntity(final String id, final String entityType,
			final Map<String, String> entity) {
		final LDUser ldUser = this.buildLDUser(id, entityType, entity);
		return this.getFeatureToggleForLDUser(ldUser);
	}

	private LDUser buildLDUser(final String id, final String entityType, final Map<String, String> entity) {
		LDUser.Builder ldUser = new LDUser.Builder(id).anonymous(false).custom("entityType", entityType);
		for (Map.Entry<String, String> entry : entity.entrySet()) {
			ldUser = ldUser.custom(entry.getKey(), entry.getValue());
		}
		return ldUser.build();
	}

	private Map<String, Boolean> getFeatureToggleForLDUser(final LDUser ldUser) {
		client.identify(ldUser);

		final FeatureFlagsState state = client.allFlagsState(ldUser);
		final Map<String, Boolean> featureToggles = new HashedMap();
		state.toValuesMap().forEach((ftKey, ftValue) -> featureToggles.put(ftKey, ftValue.booleanValue()));
		return featureToggles;
	}
}
