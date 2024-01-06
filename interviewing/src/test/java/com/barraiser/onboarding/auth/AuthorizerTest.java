/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizerTest {
	@InjectMocks
	private Authorizer authorizer;

	@Spy
	private ArrayList<ResourceAuthorizer> resourceAuthorizers;
	@Mock
	private PartnerPortalAuthorizer partnerPortalAuthorizer;

	@Before
	public void setup() {
		this.resourceAuthorizers.add(this.partnerPortalAuthorizer);
		when(this.partnerPortalAuthorizer.type()).thenReturn(PartnerPortalAuthorizer.RESOURCE_TYPE);
		doNothing().when(this.partnerPortalAuthorizer).can(any(), any(), any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfAppropriateResourceNotFound() {
		this.authorizer.can(AuthenticatedUser.builder().build(), "action", AuthorizationResourceDTO.builder()
				.type("invalid_resource")
				.build());
	}

	@Test
	public void shouldCallAppropriateResource() {
		this.authorizer.can(AuthenticatedUser.builder().userName("user_id").build(), "action",
				AuthorizationResourceDTO.builder()
						.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
						.resource("resource")
						.build());

		verify(this.partnerPortalAuthorizer).can(
				argThat(arg -> arg.getUserName().equals("user_id")),
				eq("action"),
				argThat(arg -> arg.toString().equals("resource")));
	}
}
