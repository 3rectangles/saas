/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.scheduling;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.scheduling.followup.FollowUpConstants;
import com.barraiser.onboarding.scheduling.followup.GetAdjustedFollowUpTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAdjustedFollowUpTimeTest {

	@InjectMocks
	private GetAdjustedFollowUpTime getAdjustedFollowTime;

	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Test
	public void findTimeXMinsAfterExcludingNonOpHrs() {
		when(this.appConfigProperties.getInt(FollowUpConstants.DYNAMO_NON_OPERATIONAL_TIME_START)).thenReturn(22);
		when(this.appConfigProperties.getInt(FollowUpConstants.DYNAMO_NON_OPERATIONAL_TIME_END)).thenReturn(8);

		Long epochFollowUpTime = null;
		Long expectedTimeToWait = null;
		Long actualTimeToWait = null;

		// FollowUp 15/4/22 6 am : Notification Time: 15/4/22 11 am
		epochFollowUpTime = 1649982600L;
		expectedTimeToWait = 1650000600L;
		actualTimeToWait = this.getAdjustedFollowTime.findTimeXMinsAfterExcludingNonOpHrs(3 * 60, epochFollowUpTime);
		assertEquals(expectedTimeToWait, actualTimeToWait);

		// FollowUp 15/4/22 11 pm : Notification Time: 16/4/22 10 am
		epochFollowUpTime = 1650043800L;
		expectedTimeToWait = 1650083400L;
		actualTimeToWait = this.getAdjustedFollowTime.findTimeXMinsAfterExcludingNonOpHrs(2 * 60, epochFollowUpTime);
		assertEquals(expectedTimeToWait, actualTimeToWait);

		// FollowUp 16/4/22 1am : Notification Time: 16/4/22 10 am
		epochFollowUpTime = 1650051000L;
		expectedTimeToWait = 1650083400L;
		actualTimeToWait = this.getAdjustedFollowTime.findTimeXMinsAfterExcludingNonOpHrs(2 * 60, epochFollowUpTime);
		assertEquals(expectedTimeToWait, actualTimeToWait);

		// FollowUp 16/4/22 9pm : Notification Time: 17/4/22 7 pm
		epochFollowUpTime = 1650123000L;
		expectedTimeToWait = 1650202200L;
		actualTimeToWait = this.getAdjustedFollowTime.findTimeXMinsAfterExcludingNonOpHrs(12 * 60,
				epochFollowUpTime);
		assertEquals(expectedTimeToWait, actualTimeToWait);

		// FollowUp 16/4/22 9pm : Notification Time: 17/4/22 9 am
		epochFollowUpTime = 1650123000L;
		expectedTimeToWait = 1650166200L;
		actualTimeToWait = this.getAdjustedFollowTime.findTimeXMinsAfterExcludingNonOpHrs(2 * 60, epochFollowUpTime);
		assertEquals(expectedTimeToWait, actualTimeToWait);

	}
}
