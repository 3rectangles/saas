/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.barraiser.onboarding.dal.UserBlacklistDAO;
import com.barraiser.onboarding.dal.UserBlacklistRepository;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.UserBlacklistManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserBlacklistManagerTest {

	@Mock
	private UserBlacklistRepository userBlacklistRepository;

	@InjectMocks
	private UserBlacklistManager userBlacklistManager;

	@Test
	public void testShouldBeBlacklistedWhenBlacklistStartDateAndEndDateNull() {

		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(null)
						.blacklistEndDate(null)
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				1,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldBeBlacklistedWhenBlacklistStartDateAndEndDateNotNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(Instant.now().minusSeconds(3600))
						.blacklistEndDate(Instant.now().plusSeconds(3600))
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				1,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldNotBeBlacklistedWhenBlacklistStartDateAndEndDateNotNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(Instant.now().plusSeconds(3600))
						.blacklistEndDate(Instant.now().plusSeconds(7200))
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				0,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldBeBlacklistedWhenBlacklistStartDateNullEndDateNotNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(null)
						.blacklistEndDate(Instant.now().plusSeconds(3600))
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				1,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldNotBeBlacklistedWhenBlacklistStartDateNullEndDateNotNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(null)
						.blacklistEndDate(Instant.now().minusSeconds(3600))
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				0,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldBeBlacklistedWhenBlacklistStartDateNotNullEndDateNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(Instant.now().minusSeconds(3600))
						.blacklistEndDate(null)
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				1,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldNotBeBlacklistedWhenBlacklistStartDateNotNullEndDateNull() {
		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(Instant.now().plusSeconds(3600))
						.blacklistEndDate(null)
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				0,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void shouldGetExpertsBlacklistForAllCompanies() {

		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.partnerCompanyId("")
						.blacklistStartDate(null)
						.blacklistStartDate(null)
						.build(),
				UserBlacklistDAO.builder()
						.userId("test-user-2")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.partnerCompanyId("")
						.blacklistStartDate(null)
						.blacklistStartDate(null)
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				2,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldGetBlacklistedForCompany() {

		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(null)
						.blacklistStartDate(null)
						.build(),
				UserBlacklistDAO.builder()
						.userId("test-user-2")
						.userType("EXPERT")
						.granularity("COMPANY_SPECIFIC")
						.partnerCompanyId("test_company_id")
						.blacklistStartDate(null)
						.blacklistStartDate(null)
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				2,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}

	@Test
	public void testShouldNotBeBlacklistedPostBlacklistExpiry() {

		List<UserBlacklistDAO> blacklistedUsersEntries = List.of(
				UserBlacklistDAO.builder()
						.userId("test-user-1")
						.userType("EXPERT")
						.granularity("ALL_COMPANIES")
						.blacklistStartDate(null)
						.blacklistEndDate(Instant.parse("2018-05-23T00:00:00.000Z"))
						.build(),
				UserBlacklistDAO.builder()
						.userId("test-user-2")
						.userType("EXPERT")
						.granularity("COMPANY_SPECIFIC")
						.partnerCompanyId("test_company_id")
						.blacklistStartDate(null)
						.blacklistEndDate(Instant.parse("2018-05-23T00:00:00.000Z"))
						.build());

		when(this.userBlacklistRepository.findAllByUserType("EXPERT"))
				.thenReturn(blacklistedUsersEntries);

		assertEquals(
				0,
				this.userBlacklistManager
						.getAllBlacklistedInterviewersForCompany("test_company_id")
						.size());
	}
}
