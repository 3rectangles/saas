/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserInformationManagementHelperTest {

	@InjectMocks
	private UserInformationManagementHelper userInformationManagementHelper;

	@Mock
	private AWSCognitoIdentityProvider awsCognitoIdentityProvider;

	@Mock
	private StaticAppConfigValues staticAppConfigValues;

	@Test
	public void testScenario1() {

		final AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
		adminGetUserResult.setUserAttributes(List.of(
				new AttributeType().withName("custom:partnerId").withValue("")));

		when(this.awsCognitoIdentityProvider.adminGetUser(any())).thenReturn(adminGetUserResult);
		Assert.assertEquals("partner1",
				this.userInformationManagementHelper.getUpdatedUserPartnerId("user1", "partner1"));
	}

	@Test
	public void testScenario2() {

		final AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
		adminGetUserResult.setUserAttributes(List.of(
				new AttributeType().withName("custom:partnerId").withValue("partner1")));

		when(this.awsCognitoIdentityProvider.adminGetUser(any())).thenReturn(adminGetUserResult);
		Assert.assertEquals("partner1",
				this.userInformationManagementHelper.getUpdatedUserPartnerId("user1", "partner1"));
	}

	@Test
	public void testScenario3() {

		final AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
		adminGetUserResult.setUserAttributes(List.of(
				new AttributeType().withName("custom:partnerId").withValue("partner2")));

		when(this.awsCognitoIdentityProvider.adminGetUser(any())).thenReturn(adminGetUserResult);
		Assert.assertEquals("partner2,partner1",
				this.userInformationManagementHelper.getUpdatedUserPartnerId("user1", "partner1"));
	}

	@Test
	public void testScenario4() {

		final AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
		adminGetUserResult.setUserAttributes(List.of(
				new AttributeType().withName("custom:partnerId").withValue("partner1,partner1")));

		when(this.awsCognitoIdentityProvider.adminGetUser(any())).thenReturn(adminGetUserResult);
		Assert.assertEquals("partner1,partner1",
				this.userInformationManagementHelper.getUpdatedUserPartnerId("user1", "partner1"));
	}
}
