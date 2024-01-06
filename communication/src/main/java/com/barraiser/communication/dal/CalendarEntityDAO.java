/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "calendar_entity")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CalendarEntityDAO extends BaseModel {
	@Id
	private String id;

	private String entityId;

	private String entityType;

	private Integer entityRescheduleCount;

	private String eventId;

	private String accountId;

	@Enumerated(EnumType.STRING)
	private CalendarStatus status;

	private String recipientId;
}
