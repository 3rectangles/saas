/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.audit;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.entitychange.EntityChange;
import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.UUID;

@Log4j2
@AllArgsConstructor
@Component
public class AuditListener {
	static private EntityAuditHistoryRepository entityAuditHistoryRepository;
	static private InterviewingEventProducer eventProducer;

	public final static String OPERATION_CREATE = "CREATE";
	public final static String OPERATION_UPDATE = "UPDATE";
	public final static String OPERATION_DELETE = "DELETE";

	public final static String FIELD_ID = "id";
	public final static String FIELD_OPERATED_BY = "operatedBy";
	private static ObjectMapper mapper;

	@Autowired
	public void init(
			InterviewingEventProducer eventProducer,
			EntityAuditHistoryRepository entityAuditHistoryRepository) {
		AuditListener.eventProducer = eventProducer;
		AuditListener.entityAuditHistoryRepository = entityAuditHistoryRepository;
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	@PostLoad
	private void onPostLoad(final Object entity) {
		try {
			((BaseModel) entity).setOldRawEntityState(mapper.writeValueAsString(entity));
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	@PostPersist
	public void onPostPersist(final Object entity) {
		this.audit(OPERATION_CREATE, entity);
	}

	@PostUpdate
	public void onPostUpdate(final Object entity) {
		this.audit(OPERATION_UPDATE, entity);
		// updating old entity Raw State
		try {
			((BaseModel) entity).setOldRawEntityState(mapper.writeValueAsString(entity));
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@PostRemove
	private void onPostRemove(final Object entity) {
		this.audit(OPERATION_DELETE, entity);
	}

	public void audit(final String operation, final Object entity) {

		final String entityName = entity.getClass().getAnnotation(Table.class).name();
		log.info("Operation : {}", operation);

		this.sendEntityChangeEvent(operation, entity, entityName);

		final EntityAuditHistoryDAO entityAuditHistoryDAO = EntityAuditHistoryDAO.builder()
				.id(UUID.randomUUID().toString())
				.entityName(entityName)
				.entityId(this.getFieldValue(entity, false, FIELD_ID))
				.operation(operation)
				.rawEntityState(entity)
				.operatedBy(this.getFieldValue(entity, true, FIELD_OPERATED_BY))
				.build();

		try {
			// Commenting this out as there are too many records getting saved in the table
			// and there is no particular usage for them.
			// entityAuditHistoryRepository.save(entityAuditHistoryDAO);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@SneakyThrows
	public String getFieldValue(final Object entity, final boolean findInSuperClass, final String fieldName) {
		try {
			final Class cls = entity.getClass();
			final Field field;

			if (findInSuperClass) {
				field = cls.getSuperclass().getDeclaredField(fieldName);
			} else {
				field = cls.getDeclaredField(fieldName);
			}

			field.setAccessible(true);
			final Object entityId = field.get(entity);
			return (String) entityId;
		} catch (Exception e) {
			return null;
		}
	}

	private void sendEntityChangeEvent(final String operation, final Object entity, final String entityName) {
		final Event<EntityChange> event = new Event<>();

		try {
			event.setPayload(new EntityChange()
					.entityId(this.getFieldValue(entity, false, FIELD_ID))
					.entityName(entityName)
					.rawEntityState(mapper.writeValueAsString(entity))
					.operation(operation));
			eventProducer.pushEvent(event);
		} catch (Exception e) {
			log.error("Failed to send event for entity change", e);
		}
	}
}
