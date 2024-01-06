/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.dal.StatusRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationPartnerStatusAuthorizerTest {
	@InjectMocks
	private EvaluationPartnerStatusAuthorizer evaluationPartnerStatusAuthorizer;

	@Mock
	private EvaluationRepository evaluationRepository;
	@Mock
	private PartnerCompanyRepository partnerCompanyRepository;
	@Mock
	private StatusRepository statusRepository;
	@Mock
	private PartnerPortalAuthorizer partnerPortalAuthorizer;
	@Mock
	private EvaluationStatusManager evaluationStatusManager;
	@Mock
	private JobRoleManager jobRoleManager;

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailForAnInvalidAction() {
		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().build(), "invalid_action", Map.of(
				"evaluationId", "",
				"partnerStatusId", ""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPartnerStatusIdIsNotForPartnerIfStatusIsCustomized() {
		// GIVEN
		when(this.evaluationStatusManager.isStatusCustomizedForPartner(eq("test_p_c"))).thenReturn(Boolean.TRUE);
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.jobRoleId("1")
				.build();
		when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(evaluationDAO));
		when(this.jobRoleManager.getJobRoleFromEvaluation(
				evaluationDAO)).thenReturn(Optional.of(JobRoleDAO.builder().companyId("test_c").build()));
		when(this.partnerCompanyRepository.findByCompanyId("test_c")).thenReturn(Optional.of(
				PartnerCompanyDAO.builder().id("test_p_c").build()));
		when(this.statusRepository.findById(any())).thenReturn(
				Optional.of(StatusDAO.builder().partnerId("test_p_c_1").build()));

		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().build(),
				EvaluationPartnerStatusAuthorizer.ACTION_WRITE, Map.of(
						"evaluationId", "eid",
						"partnerStatusId", "1"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfPartnerStatusIdIsNotDefaultAndStatusIsNotCustomized() {
		// GIVEN

		when(this.evaluationStatusManager.isStatusCustomizedForPartner(eq("test_p_c"))).thenReturn(Boolean.FALSE);
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.jobRoleId("1")
				.build();
		when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(evaluationDAO));
		when(this.jobRoleManager.getJobRoleFromEvaluation(
				evaluationDAO)).thenReturn(Optional.of(JobRoleDAO.builder().companyId("test_c").build()));
		when(this.partnerCompanyRepository.findByCompanyId("test_c")).thenReturn(Optional.of(
				PartnerCompanyDAO.builder().id("test_p_c").build()));
		when(this.statusRepository.findById(any())).thenReturn(
				Optional.of(StatusDAO.builder().partnerId("test_p_c_1").build()));

		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().build(),
				EvaluationPartnerStatusAuthorizer.ACTION_WRITE, Map.of(
						"evaluationId", "eid",
						"partnerStatusId", "1"));
	}

	@Test
	public void shouldPassIfPartnerStatusIdIsForPartnerAndStatusIsCustomized() {
		// GIVEN
		when(this.evaluationStatusManager.isStatusCustomizedForPartner(eq("test_p_c"))).thenReturn(Boolean.TRUE);
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.jobRoleId("1")
				.build();
		when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(evaluationDAO));
		when(this.jobRoleManager.getJobRoleFromEvaluation(
				evaluationDAO)).thenReturn(Optional.of(JobRoleDAO.builder().companyId("test_c").build()));
		when(this.partnerCompanyRepository.findByCompanyId("test_c")).thenReturn(Optional.of(
				PartnerCompanyDAO.builder().id("test_p_c").build()));
		when(this.statusRepository.findById(any())).thenReturn(
				Optional.of(StatusDAO.builder().partnerId("test_p_c").build()));

		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().build(),
				EvaluationPartnerStatusAuthorizer.ACTION_WRITE, Map.of(
						"evaluationId", "eid",
						"partnerStatusId", "1"));
	}

	@Test
	public void shouldPassIfPartnerStatusIdIsDefaultAndStatusIsNotCustomized() {
		// GIVEN
		// when(this.evaluationStatusManager.isStatusCustomizedForPartner(eq("test_p_c"))).thenReturn(Boolean.FALSE);
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.jobRoleId("1")
				.build();
		when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(evaluationDAO));
		when(this.jobRoleManager.getJobRoleFromEvaluation(
				evaluationDAO)).thenReturn(Optional.of(JobRoleDAO.builder().companyId("test_c").build()));
		when(this.partnerCompanyRepository.findByCompanyId("test_c")).thenReturn(Optional.of(
				PartnerCompanyDAO.builder().id("b").build()));
		when(this.statusRepository.findById(any())).thenReturn(
				Optional.of(StatusDAO.builder().partnerId(null).build()));

		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().build(),
				EvaluationPartnerStatusAuthorizer.ACTION_WRITE, Map.of(
						"evaluationId", "eid",
						"partnerStatusId", "1"));
	}

	@Test
	public void shouldCallPartnerPortalAuthorizerForWriteActionForWriteAccess() {
		// GIVEN
		when(this.evaluationStatusManager.isStatusCustomizedForPartner(eq("test_p_c"))).thenReturn(Boolean.TRUE);
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder()
				.jobRoleId("1")
				.build();
		when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(evaluationDAO));
		when(this.jobRoleManager.getJobRoleFromEvaluation(
				evaluationDAO)).thenReturn(Optional.of(JobRoleDAO.builder().companyId("test_c").build()));
		when(this.partnerCompanyRepository.findByCompanyId("test_c")).thenReturn(Optional.of(
				PartnerCompanyDAO.builder().id("test_p_c").build()));
		when(this.statusRepository.findById(any())).thenReturn(
				Optional.of(StatusDAO.builder().partnerId("test_p_c").build()));

		// WHEN
		this.evaluationPartnerStatusAuthorizer.can(AuthenticatedUser.builder().userName("userId").build(),
				EvaluationPartnerStatusAuthorizer.ACTION_WRITE, Map.of(
						"evaluationId", "eid",
						"partnerStatusId", "1"));

		// THEN
		verify(this.partnerPortalAuthorizer).can(argThat(arg -> arg.getUserName().equals("userId")),
				eq(PartnerPortalAuthorizer.ACTION_READ_AND_WRITE),
				argThat(arg -> arg.toString().equals("test_p_c")));
	}
}
