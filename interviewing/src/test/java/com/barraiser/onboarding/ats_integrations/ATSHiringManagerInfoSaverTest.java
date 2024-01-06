/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.jobRoleManagement.ATSPartnerRepInfo;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.HiringManagerInfoSaver;
import com.barraiser.onboarding.jobRoleManagement.UserManagement.JobRoleUserManagementUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ATSHiringManagerInfoSaverTest {

	@Mock
	private AuthorizationServiceFeignClient authorizationServiceFeignClient;

	@Mock
	private JobRoleUserManagementUtil jobRoleUserManagementUtil;

	@InjectMocks
	private HiringManagerInfoSaver atsHiringManagerInfoSaver;

	/**
	 * Case : There are no HMs already added for that job role.
	 */
	@Test
	public void testHMAdditionScenario1() {
		// GIVEN
		final String brJobRoleId = "test_jr_1";

		final Set<String> hiringManagersInputList = Set.of("br_partner_rep_id_1");

		// WHEN
		when(this.jobRoleUserManagementUtil.getAllUserWithARole(any(), any()))
				.thenReturn(List.of(""));

		doNothing().when(this.jobRoleUserManagementUtil).grantRole(any(), any(), any());

		when(this.authorizationServiceFeignClient.getActiveUserRoleMappingsForDimension(any())).thenReturn(null);

		// THEN
		List<String> resultantListOfHmsForJobRole = this.atsHiringManagerInfoSaver.manageHiringManagersInfo(brJobRoleId,
				hiringManagersInputList);

		// EXPECTED OUTPUT
		Assert.assertEquals(List.of("br_partner_rep_id_1"),
				resultantListOfHmsForJobRole);
	}

	// /**
	// * Case : There are some HMs already added for that job role.
	// */
	// @Test
	// public void testHMAdditionScenario2() {
	// // GIVEN
	// final String brJobRoleId = "test_jr_1";
	// final Map<String, List<String>> userIdToBrUserRoleIdsMapping =
	// Map.of("br_partner_rep_id_2", List.of("HM"));
	// final Map<String, String> atsToBrPartnerRepMapping =
	// Map.of("ats_partner_rep_id_1", "br_partner_rep_id_1",
	// "ats_partner_rep_id_2", "br_partner_rep_id_2",
	// "ats_partner_rep_id_3", "br_partner_rep_id_3");
	// final List<ATSPartnerRepInfo> hiringManagersInputList = List.of(
	// ATSPartnerRepInfo.builder().atsPartnerRepId("ats_partner_rep_id_1").build(),
	// ATSPartnerRepInfo.builder().atsPartnerRepId("ats_partner_rep_id_2").build());
	//
	// // THEN
	// List<String> resultantListOfHmsForJobRole =
	// this.atsHiringManagerInfoSaver.manageHiringManagersInfo(brJobRoleId,
	// userIdToBrUserRoleIdsMapping,
	// atsToBrPartnerRepMapping, hiringManagersInputList);
	//
	// // EXPECTED OUTPUT
	// Assert.assertEquals(List.of("br_partner_rep_id_2", "br_partner_rep_id_1"),
	// resultantListOfHmsForJobRole);
	// }
	//
	// /**
	// * Case : A user that was not successfully added as a partner rep (means ats
	// * to
	// * br partner rep mapping will be missing) , was sent to be added as HM
	// * Expectation : it will just be skipped in the process of addition as HM
	// */
	// @Test
	// public void testHMAdditionScenario3() {
	// // GIVEN
	// final String brJobRoleId = "test_jr_1";
	// final Map<String, List<String>> userIdToBrUserRoleIdsMapping = Map.of();
	// final Map<String, String> atsToBrPartnerRepMapping =
	// Map.of("ats_partner_rep_id_1", "br_partner_rep_id_1",
	// "ats_partner_rep_id_2", "br_partner_rep_id_2");
	// final List<ATSPartnerRepInfo> hiringManagersInputList = List.of(
	// ATSPartnerRepInfo.builder().atsPartnerRepId("ats_partner_rep_id_3").build());
	//
	// // THEN
	// List<String> resultantListOfHmsForJobRole =
	// this.atsHiringManagerInfoSaver.manageHiringManagersInfo(brJobRoleId,
	// userIdToBrUserRoleIdsMapping,
	// atsToBrPartnerRepMapping, hiringManagersInputList);
	//
	// // EXPECTED OUTPUT
	// Assert.assertEquals(List.of(), resultantListOfHmsForJobRole);
	// }
	//
	// /**
	// * Case :There are some users added already for that Job role but are not HMs.
	// */
	// @Test
	// public void testHMAdditionScenario4() {
	// // GIVEN
	// final String brJobRoleId = "test_jr_1";
	// final Map<String, List<String>> userIdToBrUserRoleIdsMapping =
	// Map.of("br_partner_rep_id_1",
	// List.of("RECRUITER"));
	// final Map<String, String> atsToBrPartnerRepMapping =
	// Map.of("ats_partner_rep_id_1", "br_partner_rep_id_1",
	// "ats_partner_rep_id_2", "br_partner_rep_id_2",
	// "ats_partner_rep_id_3", "br_partner_rep_id_3");
	// final List<ATSPartnerRepInfo> hiringManagersInputList = List.of(
	// ATSPartnerRepInfo.builder().atsPartnerRepId("ats_partner_rep_id_3").build());
	//
	// // THEN
	// List<String> resultantListOfHmsForJobRole =
	// this.atsHiringManagerInfoSaver.manageHiringManagersInfo(brJobRoleId,
	// userIdToBrUserRoleIdsMapping,
	// atsToBrPartnerRepMapping, hiringManagersInputList);
	//
	// // EXPECTED OUTPUT
	// Assert.assertEquals(List.of("br_partner_rep_id_3"),
	// resultantListOfHmsForJobRole);
	// }
}
