/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationManagerTest {
	@Mock
	private EvaluationRepository evaluationRepository;

	@Mock
	private PartnerCompanyRepository partnerCompanyRepository;

	@Mock
	private JobRoleManager jobRoleManager;

	@InjectMocks
	private EvaluationManager evaluationManager;

	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Test
	public void getPartnerCompanyWhenEvaluationDAONotPresent() {
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation("1");
		assertEquals(null, partnerId);
	}

	@Test
	public void getPartnerCompanyWhenEvaluationDAOReturnsEmpty() {
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.empty());
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation("1");
		assertEquals(null, partnerId);
	}

	@Test
	public void shouldReturnNullIfPartnerCompanyNotPresent() {
		when(this.evaluationRepository.findById(any()))
				.thenReturn(Optional.ofNullable(EvaluationDAO.builder().build()));
		when(this.jobRoleManager.getJobRole(any(), any()))
				.thenReturn(Optional.ofNullable(JobRoleDAO.builder().companyId("1").build()));
		when(this.partnerCompanyRepository.findByCompanyId(any()))
				.thenReturn(Optional.empty());

		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation("1");
		assertEquals(null, partnerId);
	}

	@Test
	public void shouldNotReturnNullIfPartnerCompanyIsPresent() {
		when(this.evaluationRepository.findById(any()))
				.thenReturn(Optional.ofNullable(EvaluationDAO.builder().build()));
		when(this.jobRoleManager.getJobRole(any(), any()))
				.thenReturn(Optional.ofNullable(JobRoleDAO.builder().companyId("1").build()));
		when(this.partnerCompanyRepository.findByCompanyId(any()))
				.thenReturn(Optional.ofNullable(PartnerCompanyDAO.builder().id("1").build()));

		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation("1");
		assertEquals("1", partnerId);
	}

	@Test
	public void shouldReturnTrueForDemoCompany() {
		when(this.appConfigProperties.getString(PartnerEmployeeWhiteLister.DYNAMO_DEMO_COMPANIES))
				.thenReturn("1");
		final Boolean isDemoCompany = this.evaluationManager.isDemoCompany("1");
		assertTrue(isDemoCompany);
	}

	@Test
	public void shouldReturnFalseForDemoCompany() {
		when(this.appConfigProperties.getString(PartnerEmployeeWhiteLister.DYNAMO_DEMO_COMPANIES))
				.thenReturn("1");
		final Boolean isDemoCompany = this.evaluationManager.isDemoCompany("2");
		assertFalse(isDemoCompany);
	}
}
