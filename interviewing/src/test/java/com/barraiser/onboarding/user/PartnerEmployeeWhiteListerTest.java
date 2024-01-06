/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PartnerEmployeeWhiteListerTest {

	@Mock
	private UserInformationManagementHelper userManagement;
	@Mock
	private PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;
	@Mock
	private PartnerBlacklistedDomainsRepository partnerBlacklistedDomainsRepository;
	@Mock
	private DynamicAppConfigProperties appConfigProperties;
	@Mock
	private PartnerRepsRepository partnerRepsRepository;

	@InjectMocks
	private PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;

	@Test
	public void shouldAddUser() {
		final String email = "barraiser.com@barraisertest.com";
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("test_p_c"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraisertest.com").build()));
		when(this.userManagement.getOrCreateUserByEmail(email))
				.thenReturn(UserDetailsDAO.builder().id("2").build());
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "test_p_c");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.userManagement).updateUserAttributes(eq("2"),
				argThat(arg -> arg.get("custom:partnerId").equals("test_p_c")));
	}

	@Test
	public void shouldReturnFalseIfPartnerIdIsDifferent() {
		final AuthenticatedUser user = AuthenticatedUser.builder().email("barraiser@barraiser1.com")
				.roles(List.of(UserRole.PARTNER_EMPLOYEE)).partnerId("p2").build();
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p1"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		final boolean isAllowed = this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(user.getEmail(), "p1");
		assertFalse(isAllowed);
	}

	@Test
	public void shouldReturnTrueIfPartnerEmployeeForSamePartner() {
		final AuthenticatedUser user = AuthenticatedUser.builder().email("barraiser@barraiser.com")
				.roles(List.of(UserRole.PARTNER_EMPLOYEE)).partnerId("p1").build();
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p1"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		final boolean isAllowed = this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(user.getEmail(), "p1");
		assertTrue(isAllowed);
	}

	@Test
	public void shouldNotAddUserIfAlreadyExists() {
		final String email = "barraiser.com@barraiser.com";
		when(this.userManagement.doesUserExistsByEmail(email))
				.thenReturn(true);
		when(this.userManagement.getOrCreateUserByEmail(email)).thenReturn(UserDetailsDAO.builder().id("1").build());
		when(this.userManagement.getRolesOfUser("1")).thenReturn(List.of());
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("test_p_c"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "test_p_c");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.partnerWhitelistedDomainsRepository, never()).findByEmailDomainIgnoreCase(any());
	}

	@Test
	public void shouldNotAddPartnerIdIfPartnerIdAlreadyExists() {
		final String email = "barraiser.com@barraiser.com";
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p2"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		when(this.userManagement.getOrCreateUserByEmail(email))
				.thenReturn(UserDetailsDAO.builder().id("2").build());
		when(this.userManagement.getUserAttributes("2")).thenReturn(Map.of("custom:partnerId", "p1,p2"));
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "p2");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.userManagement).addUserRole(eq("2"), eq(UserRole.PARTNER_EMPLOYEE));
		verify(this.userManagement).updateUserAttributes(eq("2"),
				argThat(arg -> arg.get("custom:partnerId").equals("p1,p2")));
	}

	@Test
	public void shouldAddPartnerIdIfPartnerIdIsNull() {
		final String email = "barraiser.com@barraiser.com";
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p2"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		when(this.userManagement.getOrCreateUserByEmail(email))
				.thenReturn(UserDetailsDAO.builder().id("2").build());
		when(this.userManagement.getUserAttributes("2")).thenReturn(Map.of("x", "y"));
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "p2");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.userManagement).addUserRole(eq("2"), eq(UserRole.PARTNER_EMPLOYEE));
		verify(this.userManagement).updateUserAttributes(eq("2"),
				argThat(arg -> arg.get("custom:partnerId").equals("p2")));
	}

	@Test
	public void shouldAddPartnerIdIfPartnerIdIsEmpty() {
		final String email = "barraiser.com@barraiser.com";
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p2"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		when(this.userManagement.getOrCreateUserByEmail(email))
				.thenReturn(UserDetailsDAO.builder().id("2").build());
		when(this.userManagement.getUserAttributes("2")).thenReturn(Map.of("custom:partnerId", ""));
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "p2");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.userManagement).addUserRole(eq("2"), eq(UserRole.PARTNER_EMPLOYEE));
		verify(this.userManagement).updateUserAttributes(eq("2"),
				argThat(arg -> arg.get("custom:partnerId").equals("p2")));
	}

	@Test
	public void shouldAddPartnerIdIfPartnerIdIsNotPresent() {
		final String email = "barraiser.com@barraiser.com";
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId("p3"))
				.thenReturn(List.of(PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build()));
		when(this.userManagement.getOrCreateUserByEmail(email))
				.thenReturn(UserDetailsDAO.builder().id("2").build());
		when(this.userManagement.getUserAttributes("2")).thenReturn(Map.of("custom:partnerId", "p1,p2"));
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, "p3");
		verify(this.userManagement).getOrCreateUserByEmail(argThat(arg -> arg.equals(email)));
		verify(this.userManagement).addUserRole(eq("2"), eq(UserRole.PARTNER_EMPLOYEE));
		verify(this.userManagement).updateUserAttributes(eq("2"),
				argThat(arg -> arg.get("custom:partnerId").equals("p1,p2,p3")));
	}

	@Test
	public void testBlackListedDomain() {
		// GIVEN
		final String firstDomain = "domain1.com";
		final String secondDomain = "domain2.com";
		final String partnerId = "a partner id";

		when(this.partnerBlacklistedDomainsRepository
				.findByPartnerIdAndEmailDomain(partnerId, firstDomain))
						.thenReturn(Optional.of(PartnerBlackListedDomainDAO.builder()
								.emailDomain(firstDomain)
								.partnerId(partnerId)
								.build()));
		when(this.partnerBlacklistedDomainsRepository
				.findByPartnerIdAndEmailDomain(partnerId, secondDomain))
						.thenReturn(Optional.empty());

		// WHEN
		final boolean isFirstDomainBlackListed = this.partnerEmployeeWhiteLister
				.isUserDomainBlackListedForPartner("user@domain1.com", partnerId);
		final boolean isSecondDomainBlackListed = this.partnerEmployeeWhiteLister
				.isUserDomainBlackListedForPartner("user@domain2.com", partnerId);

		// THEN
		assertTrue(isFirstDomainBlackListed);
		assertFalse(isSecondDomainBlackListed);
	}
}
