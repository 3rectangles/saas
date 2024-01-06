/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@SuperBuilder(toBuilder = true)
@MappedSuperclass
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseModel {

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Instant createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Instant updatedOn;
}
