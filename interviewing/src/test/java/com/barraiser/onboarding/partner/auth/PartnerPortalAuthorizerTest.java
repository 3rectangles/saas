/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.commons.auth.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerPortalAuthorizerTest {
	@InjectMocks
	private PartnerPortalAuthorizer partnerPortalAuthorizer;

	@Mock
	private PartnerRepsRepository partnerRepsRepository;

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailForAnInvalidAction() {
		// WHEN
		this.partnerPortalAuthorizer.can(AuthenticatedUser.builder().build(), "invalid_action", "");
	}

	@Test
	public void shouldAllowOpsForReadAndWriteAccess() {
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.OPS))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test
	public void shouldAllowAdminForReadAndWriteAccess() {
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.ADMIN))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowIfRoleNotPartnerForReadAndWriteAccess() {
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.CANDIDATE))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowIfNotInPartnerRepsForReadAndWriteAccess() {
		// GIVEN
		when(this.partnerRepsRepository.findById(any())).thenReturn(Optional.empty());
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.PARTNER))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowIfBelongsToSomeOtherPartnerForReadAndWriteAccess() {
		// GIVEN
		when(this.partnerRepsRepository.findById(any())).thenReturn(Optional.of(
				PartnerRepsDAO.builder()
						.id("user_id")
						.partnerId("some_other_partner_id")
						.build()));
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.PARTNER))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test
	public void shouldAllowIfBelongsToPartnerForReadAndWriteAccess() {
		// GIVEN
		when(this.partnerRepsRepository.findById(any())).thenReturn(Optional.of(
				PartnerRepsDAO.builder()
						.id("user_id")
						.partnerId("partner_id")
						.build()));
		// WHEN
		this.partnerPortalAuthorizer.can(
				AuthenticatedUser.builder()
						.userName("user_id")
						.roles(List.of(UserRole.PARTNER))
						.build(),
				PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
				"partner_id");
	}

	@Test
	public void shouldReturnFalseIfPartnerRepIsNull() {
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1").build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertFalse(isPartnerRep);
	}

	@Test
	public void shouldReturnFalseIfPartnerRepIsEmpty() {
		when(this.partnerRepsRepository.findById(any()))
				.thenReturn(Optional.empty());
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1").build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertFalse(isPartnerRep);
	}

	@Test
	public void shouldReturnFalseIfPartnerIdIsNull() {
		when(this.partnerRepsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(PartnerRepsDAO.builder().build()));
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1").build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertFalse(isPartnerRep);
	}

	@Test
	public void shouldReturnFalseIfPartnerIdIsNotSame() {
		when(this.partnerRepsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(PartnerRepsDAO.builder().partnerId("2").build()));
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1").build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertFalse(isPartnerRep);
	}

	@Test
	public void shouldReturnFalseIfPartnerRepsIsNotPartner() {
		when(this.partnerRepsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(PartnerRepsDAO.builder().partnerId("1").build()));
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1")
				.roles(Arrays.asList(UserRole.CANDIDATE)).build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertFalse(isPartnerRep);
	}

	@Test
	public void shouldReturnFalseIfPartnerRepsIsPartner() {
		when(this.partnerRepsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(PartnerRepsDAO.builder().partnerId("1").build()));
		final AuthenticatedUser user = AuthenticatedUser.builder().userName("1")
				.roles(Arrays.asList(UserRole.CANDIDATE, UserRole.PARTNER)).build();
		final boolean isPartnerRep = this.partnerPortalAuthorizer.isPartnerRep(user, "1");
		assertTrue(isPartnerRep);
	}
}
