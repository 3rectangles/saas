/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ZoomManagerTest {
	@Mock
	private DynamicAppConfigProperties dynamicAppConfigProperties;
	@InjectMocks
	private ZoomManager zoomManager;

	@Test
	public void testGetOneZoomAccounts() {
		// GIVEN
		when(this.dynamicAppConfigProperties.getListOfString(ZoomManager.ZOOM_ACCOUNTS_KEY))
				.thenReturn(List.of("ac1", "ac2", "ac3", "ac4"));

		// WHEN
		final List<String> usedAccounts = List.of("ac1", "ac2", "ac1", "ac2", "ac1", "ac3");
		final String accountTouse = this.zoomManager.getZoomAccountToUse(usedAccounts);

		// THEN
		assertNotEquals("ac1", accountTouse);
		assertNotEquals("ac2", accountTouse);
	}

	@Test
	public void testGetOneZoomAccountWhenusedAccountsAreNotPartOf() {
		// GIVEN
		when(this.dynamicAppConfigProperties.getListOfString(ZoomManager.ZOOM_ACCOUNTS_KEY))
				.thenReturn(List.of("ac4", "ac5", "ac6", "ac7"));

		// WHEN
		final List<String> usedAccounts = List.of("ac1", "ac2", "ac1", "ac2", "ac1");
		final String accountTouse = this.zoomManager.getZoomAccountToUse(usedAccounts);

		// THEN
		assertNotEquals("ac1", accountTouse);
		assertNotEquals("ac2", accountTouse);
	}

	@Test
	public void testPreferenceGivenToAccountsNotUsed() {
		// GIVEN
		when(this.dynamicAppConfigProperties.getListOfString(ZoomManager.ZOOM_ACCOUNTS_KEY))
				.thenReturn(List.of("ac1", "ac4", "ac5", "ac6", "ac7"));

		// WHEN
		final List<String> usedAccounts = List.of("ac1");
		final String accountTouse = this.zoomManager.getZoomAccountToUse(usedAccounts);

		// THEN
		assertNotEquals("ac1", accountTouse);
	}

	@Test
	public void testWhenAllAccountsAreUsedAtLeastOnce() {
		// GIVEN
		when(this.dynamicAppConfigProperties.getListOfString(ZoomManager.ZOOM_ACCOUNTS_KEY))
				.thenReturn(List.of("ac1", "ac4", "ac5", "ac6", "ac7"));

		// WHEN
		final List<String> usedAccounts = List.of("ac1", "ac4", "ac5", "ac6", "ac7");
		final String accountToUse = this.zoomManager.getZoomAccountToUse(usedAccounts);

		// THEN
		assertNotNull(accountToUse);
	}

	@Test(expected = NoZoomAccountAvailableForInterviewException.class)
	public void testWhenNoAccountIsAvailable() {
		// GIVEN
		when(this.dynamicAppConfigProperties.getListOfString(ZoomManager.ZOOM_ACCOUNTS_KEY))
				.thenReturn(List.of("ac1", "ac2", "ac3"));

		// WHEN
		final List<String> usedAccounts = List.of("ac1", "ac2", "ac1", "ac2", "ac1", "ac3", "ac3");
		final String accountTouse = this.zoomManager.getZoomAccountToUse(usedAccounts);

		// THEN
		assertNotEquals("ac1", accountTouse);
		assertNotEquals("ac2", accountTouse);
	}
}
