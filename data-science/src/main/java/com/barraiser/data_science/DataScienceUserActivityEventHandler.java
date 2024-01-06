/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.userclick.UserClick;
import com.barraiser.data_science.dal.DataScienceUserActivityDAO;
import com.barraiser.data_science.dal.DataScienceUserActivityRepository;
import com.barraiser.data_science.events.DataScienceConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class DataScienceUserActivityEventHandler implements EventListener<DataScienceConsumer> {
	private final ObjectMapper objectMapper;
	private final DataScienceUserActivityRepository dataScienceUserActivityRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(UserClick.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final UserClick userClick = this.objectMapper
				.convertValue(
						event.getPayload(),
						UserClick.class);

		this.saveDataScienceUserActivityEventToDatabase(userClick);
	}

	private void saveDataScienceUserActivityEventToDatabase(final UserClick userClick) throws Exception {
		List<DataScienceEntityType> dataScienceEntityTypes = Arrays.asList(DataScienceEntityType.values());

		List<String> dataScienceEntityTypeNames = dataScienceEntityTypes
				.stream()
				.map(dataScienceEntityType -> dataScienceEntityType.getValue())
				.collect(Collectors.toList());

		if (dataScienceEntityTypeNames.contains(userClick.getContext())) {
			log.info(String.format(
					"Captured a %s event and logging this to DS user activity table",
					userClick.getContext()));

			final DataScienceUserActivityDAO dataScienceUserActivityDAO = DataScienceUserActivityDAO
					.builder()
					.id(UUID
							.randomUUID()
							.toString())
					.context(userClick.getContext())
					.payload(this.objectMapper
							.readTree(userClick.getPayload()))
					.build();

			this.dataScienceUserActivityRepository
					.save(dataScienceUserActivityDAO);
		}
	}
}
