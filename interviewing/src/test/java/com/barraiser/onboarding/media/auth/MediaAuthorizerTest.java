/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.media.auth;

import com.barraiser.media_management.dal.MediaDAO;
import com.barraiser.media_management.repository.MediaRepository;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.enums.Action;
import com.barraiser.commons.auth.AuthenticatedUser;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;

import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.barraiser.commons.auth.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MediaAuthorizerTest {

	@Mock
	private MediaRepository mediaRepository;

	@Mock
	private EvaluationManager evaluationManager;

	@Mock
	private PartnerPortalAuthorizer partnerPortalAuthorizer;

	@Mock
	private PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;

	@Mock
	private InterViewRepository interViewRepository;

	@InjectMocks
	private MediaAuthorizer mediaAuthorizer;

	@Test
	public void testAdminShouldBeAbleToReadMedia() {
		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().userName("authenticatedUser")
				.roles(List.of(UserRole.ADMIN)).build();
		final Map<String, String> resourceAttributeMap = Map.of("anyKey", "anyValue");
		this.mediaAuthorizer.can(authenticatedUser, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testOpsShouldBeAbleToReadMedia() {
		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().userName("authenticatedUser")
				.roles(List.of(UserRole.OPS)).build();
		final Map<String, String> resourceAttributeMap = Map.of("anyKey", "anyValue");
		this.mediaAuthorizer.can(authenticatedUser, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testQcShouldBeAbleToReadMedia() {
		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().userName("authenticatedUser")
				.roles(List.of(UserRole.QC)).build();
		final Map<String, String> resourceAttributeMap = Map.of("anyKey", "anyValue");
		this.mediaAuthorizer.can(authenticatedUser, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidActionSupplied() {
		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder().userName("authenticatedUser")
				.roles(List.of(UserRole.OPS)).build();
		final Map<String, String> resourceAttributeMap = Map.of("anyKey", "anyValue");
		this.mediaAuthorizer.can(authenticatedUser, "Invalid action", resourceAttributeMap);
	}

	@Test(expected = AuthorizationException.class)
	public void testPartnerShouldNotBeAbleToReadMediaOfOtherPartnerEvaluation() {

		when(this.mediaRepository.findById(any()))
				.thenReturn(Optional.of(MediaDAO.builder().context("INTERVIEW_RECORDING").build()));

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().evaluationId("eval_1").build()));
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");

		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any())).thenReturn(false);

		when(this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(any(), any()))
				.thenReturn(false);

		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		final Map<String, String> resourceAttributeMap = Map.of("resourceId", "media1");

		this.mediaAuthorizer.can(user, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testPartnerShouldBeAbleToReadMediaOfOwnEvaluation() {
		when(this.mediaRepository.findById(any()))
				.thenReturn(Optional.of(MediaDAO.builder().context("INTERVIEW_RECORDING").build()));

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().evaluationId("eval_1").build()));
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("partner_company_1");

		when(this.partnerPortalAuthorizer.isPartnerRep(any(), any())).thenReturn(true);

		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		final Map<String, String> resourceAttributeMap = Map.of("resourceId", "1");

		this.mediaAuthorizer.can(user, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testCandidateShouldBeAbleToReadMediaFromDemoCompanyEvaluation() {

		when(this.mediaRepository.findById(any()))
				.thenReturn(Optional.of(MediaDAO.builder().context("INTERVIEW_RECORDING").build()));

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().evaluationId("demo_eval_1").build()));
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("demo_partner_company_1");

		when(this.evaluationManager.isDemoCompany(any())).thenReturn(true);

		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.CANDIDATE)).build();
		final Map<String, String> resourceAttributeMap = Map.of("resourceId", "1");

		this.mediaAuthorizer.can(user, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testAnyPartnerShouldBeAbleToReadMediaFromDemoCompanyEvaluation() {

		when(this.mediaRepository.findById(any()))
				.thenReturn(Optional.of(MediaDAO.builder().context("INTERVIEW_RECORDING").build()));

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().evaluationId("demo_eval_1").build()));
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("demo_partner_company_1");

		when(this.evaluationManager.isDemoCompany(any())).thenReturn(true);

		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER)).build();
		final Map<String, String> resourceAttributeMap = Map.of("resourceId", "1");

		this.mediaAuthorizer.can(user, Action.READ.getValue(), resourceAttributeMap);
	}

	@Test
	public void testAnyPartnerEmployeeShouldBeAbleToReadMediaFromDemoCompanyEvaluation() {

		when(this.mediaRepository.findById(any()))
				.thenReturn(Optional.of(MediaDAO.builder().context("INTERVIEW_RECORDING").build()));

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().evaluationId("demo_eval_1").build()));
		when(this.evaluationManager.getPartnerCompanyForEvaluation(any())).thenReturn("demo_partner_company_1");

		when(this.evaluationManager.isDemoCompany(any())).thenReturn(true);

		final AuthenticatedUser user = AuthenticatedUser.builder().roles(Arrays.asList(UserRole.PARTNER_EMPLOYEE))
				.build();
		final Map<String, String> resourceAttributeMap = Map.of("resourceId", "1");

		this.mediaAuthorizer.can(user, Action.READ.getValue(), resourceAttributeMap);
	}

}
