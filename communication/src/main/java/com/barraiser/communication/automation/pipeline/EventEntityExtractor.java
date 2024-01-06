/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline;

import com.barraiser.common.dal.EventToEntityDAO;
import com.barraiser.common.dal.EventToEntityRepository;
import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.commons.eventing.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class EventEntityExtractor {
	private final EventToEntityRepository eventToEntityRepository;

	public Entity getEntity(final Event event) {
		// TODO: error handling?
		final Optional<EventToEntityDAO> eventToEntityDAO = this.eventToEntityRepository
				.findByEventType(event.getEventType());
		if (eventToEntityDAO.isEmpty()) {
			return null;
		}

		Entity entity = Entity.builder()
				.type(eventToEntityDAO.get().getEntityType())
				.id(this.extractEntityId(event.getPayload(), eventToEntityDAO.get().getEntityIdPath()))
				.build();

		if (entity.getType().equals(EntityType.ENTITY)) {

			List<String> entityTypePath = new ArrayList<>(List.of("entity", "entityType"));
			entity = entity.toBuilder()
					.type(EntityType.valueOf(this.extractEntityId(event.getPayload(), entityTypePath)))
					.build();
		}
		// Event must contain partnerId in the payload root
		final String partnerId = ((Map<String, Object>) event.getPayload()).get("partnerId").toString();
		if (partnerId == null) {
			// TODO: error msg?
			throw new IllegalArgumentException();
		}
		return entity.toBuilder().partnerId(partnerId).build();
	}

	private String extractEntityId(final Object payload, final List<String> path) {
		Object current = payload;
		for (int i = 0; i < path.size(); ++i) {
			current = ((Map<String, Object>) current).get(path.get(i));
		}
		return current.toString();
	}
}
