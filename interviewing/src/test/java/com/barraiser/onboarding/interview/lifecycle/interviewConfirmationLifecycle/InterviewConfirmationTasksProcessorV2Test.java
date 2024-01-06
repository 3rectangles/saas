/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.lifecycle.interviewConfirmationLifecycle;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.scheduling.confirmation.util.ConfirmationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.barraiser.onboarding.scheduling.confirmation.ConfirmationConstants.DYNAMO_NON_OPERATIONAL_TIME_END;
import static com.barraiser.onboarding.scheduling.confirmation.ConfirmationConstants.DYNAMO_NON_OPERATIONAL_TIME_START;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewConfirmationTasksProcessorV2Test {

	@InjectMocks
	private ConfirmationUtils confirmationUtils;

	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Test
	public void findTime12HrsBefore() {

		when(this.appConfigProperties.getInt(DYNAMO_NON_OPERATIONAL_TIME_START)).thenReturn(22);
		when(this.appConfigProperties.getInt(DYNAMO_NON_OPERATIONAL_TIME_END)).thenReturn(8);

		Long epochInterviewTime = null;
		Long expectedTimeToWaitTill12Hrs = null;
		Long timeToWaitTill12Hrs = null;

		// Interview 28/2/22 1 am : Notification Time: 27/2/22 10 am
		epochInterviewTime = 1645990200L;
		expectedTimeToWaitTill12Hrs = 1645936200L;
		timeToWaitTill12Hrs = this.confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(12 * 60, epochInterviewTime,
				"Asia/Kolkata");
		assertEquals(expectedTimeToWaitTill12Hrs, timeToWaitTill12Hrs);

		// Interview 28/2/22 11 am : Notification Time: 27/2/22 1 PM
		epochInterviewTime = 1646026200L;
		expectedTimeToWaitTill12Hrs = 1645947000L;
		timeToWaitTill12Hrs = this.confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(12 * 60, epochInterviewTime,
				"Asia/Kolkata");
		assertEquals(expectedTimeToWaitTill12Hrs, timeToWaitTill12Hrs);

		// Interview 28/2/22 9 am : Notification Time: 27/2/22 11 am
		epochInterviewTime = 1646019000L;
		expectedTimeToWaitTill12Hrs = 1645939800L;
		timeToWaitTill12Hrs = this.confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(12 * 60, epochInterviewTime,
				"Asia/Kolkata");
		assertEquals(expectedTimeToWaitTill12Hrs, timeToWaitTill12Hrs);

		// Interview 28/2/22 9 am : Notification Time: 28/2/22 8:30 am
		epochInterviewTime = 1646019000L;
		expectedTimeToWaitTill12Hrs = 1646017200L;
		timeToWaitTill12Hrs = this.confirmationUtils.findTimeXMinsBeforeExcludingNonOpHrs(30, epochInterviewTime,
				"Asia/Kolkata");
		assertEquals(expectedTimeToWaitTill12Hrs, timeToWaitTill12Hrs);

	}

}
