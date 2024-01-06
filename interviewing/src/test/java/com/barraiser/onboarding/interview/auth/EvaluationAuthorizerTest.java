/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.auth;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.interview.evaluation.auth.EvaluationAuthorizer;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.barraiser.commons.auth.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationAuthorizerTest {
	@Mock
	private EvaluationRepository evaluationRepository;
	@Mock
	private PartnerPortalAuthorizer partnerPortalAuthorizer;
	@Mock
	private PartnerCompanyRepository partnerCompanyRepository;
	@Mock
	private PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	@Mock
	private JobRoleRepository jobRoleRepository;
	@InjectMocks
	private EvaluationAuthorizer evaluationAuthorizer;
	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Mock
	private EvaluationManager evaluationManager;

	public EvaluationAuthorizerTest() {
	}

	@Before
	public void setup() {
		// We are testing a separate case when domain is blacklisted, hence one setup
		// for all cases.
		when(this.partnerEmployeeWhiteLister.isUserDomainBlackListedForPartner(anyString(), anyString()))
				.thenReturn(Boolean.FALSE);
	}

	@Test
	public void adminShouldReadEvaluation() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.ADMIN)).build();
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void opsShouldReadEvaluation() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.OPS)).build();
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void partnerRepsCannotReadOtherEvaluation() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.CANDIDATE)).build();
		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any()))
				.thenReturn(false);
		when(this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(any(), any()))
				.thenReturn(true);

		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");

		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void userCannotReadOtherEvaluation() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any()))
				.thenReturn(true);

		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");

		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test(expected = AuthorizationException.class)
	public void userCannotReadAndNotPartnerReps() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.CANDIDATE)).build();
		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any()))
				.thenReturn(false);
		when(this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(any(), any()))
				.thenReturn(false);

		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void checkPartnerCompanyWithEvaluationIdNull() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any()))
				.thenReturn(true);
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void qcShouldReadEvaluation() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.QC)).build();
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void candidateShouldReadDemo() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.CANDIDATE)).build();

		when(this.evaluationManager.isDemoCompany(any())).thenReturn(Boolean.TRUE);
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void otherPartnersShouldReadDemo() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		when(this.evaluationManager.isDemoCompany(any())).thenReturn(Boolean.TRUE);
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test
	public void otherPartnerEmployeeShouldReadDemo() {
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER_EMPLOYEE))
				.build();
		when(this.evaluationManager.isDemoCompany(any())).thenReturn(Boolean.TRUE);
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());
	}

	@Test(expected = AuthorizationException.class)
	public void blackListedDomainShouldNotBeAbleToRead() {
		// GIVEN
		final AuthenticatedUser user = AuthenticatedUser.builder().roles(List.of(UserRole.PARTNER)).build();
		when(this.partnerEmployeeWhiteLister.isUserDomainBlackListedForPartner(anyString(), anyString()))
				.thenReturn(Boolean.TRUE);

		// WHEN
		this.evaluationAuthorizer.can(
				user, EvaluationAuthorizer.ACTION_READ, Evaluation.builder().id("1").build());

		// THEN
	}
}
