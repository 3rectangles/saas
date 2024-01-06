/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.common.graphql.input.availability.UpdateRecurringAvailabilityInput;
import com.barraiser.common.graphql.types.availability.RecurringAvailabilitySlot;
import com.barraiser.onboarding.dal.RecurringAvailabilityDAO;
import com.barraiser.onboarding.dal.RecurringAvailabilityRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.validation.exception.validator.Validator;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
public class UpdateRecurringAvailabilityMutationTest {

	@Mock
	private AvailabilityManager availabilityManager;

	@Mock
	private RecurringAvailabilityRepository recurringAvailabilityRepository;

	@Mock
	private GraphQLUtil graphQLUtil;

	@Mock
	private Validator validator;

	@Mock
	private DataFetchingEnvironment environment;

	@Captor
	private ArgumentCaptor<Map<DayOfTheWeek, List<RecurringAvailabilityDAO>>> recurringAvailabilityCaptor;

	@InjectMocks
	private UpdateRecurringAvailabilityMutation updateRecurringAvailabilityMutation;

	@Test
	public void shouldGroupAvailabilitiesDaywise() {

		when(this.graphQLUtil.getInput(any(), any()))
				.thenReturn(
						UpdateRecurringAvailabilityInput.builder()
								.slots(
										List.of(RecurringAvailabilitySlot.builder()
												.dayOfTheWeek(DayOfTheWeek.MON)
												.startTime(100)
												.endTime(200)
												.build(),
												RecurringAvailabilitySlot.builder()
														.dayOfTheWeek(DayOfTheWeek.MON)
														.startTime(100)
														.endTime(200)
														.build(),
												RecurringAvailabilitySlot.builder()
														.dayOfTheWeek(DayOfTheWeek.SUN)
														.startTime(100)
														.endTime(200)
														.build(),
												RecurringAvailabilitySlot.builder()
														.dayOfTheWeek(DayOfTheWeek.FRI)
														.startTime(100)
														.endTime(200)
														.build()))
								.build());

		this.updateRecurringAvailabilityMutation.fetch(this.environment, AuthorizationResult.builder().build());

		verify(this.availabilityManager).deleteOverlappingCustomAvailabilities(any(),
				this.recurringAvailabilityCaptor.capture(), any(), any());
		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilites = this.recurringAvailabilityCaptor
				.getValue();

		Assert.assertEquals(3, daywiseRecurringAvailabilites.size());
		Assert.assertEquals(Boolean.TRUE, daywiseRecurringAvailabilites.containsKey(DayOfTheWeek.MON));
		Assert.assertEquals(Boolean.TRUE, daywiseRecurringAvailabilites.containsKey(DayOfTheWeek.SUN));
		Assert.assertEquals(Boolean.TRUE, daywiseRecurringAvailabilites.containsKey(DayOfTheWeek.FRI));
		Assert.assertEquals(2, daywiseRecurringAvailabilites.get(DayOfTheWeek.MON).size());
		Assert.assertEquals(1, daywiseRecurringAvailabilites.get(DayOfTheWeek.SUN).size());
		Assert.assertEquals(1, daywiseRecurringAvailabilites.get(DayOfTheWeek.FRI).size());
	}

}
