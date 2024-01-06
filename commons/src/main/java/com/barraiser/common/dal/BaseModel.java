/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.dal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import java.time.Instant;

/**
 * TODO : Add creation source and creation source meta to base model
 */
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

	@Transient
	private String oldRawEntityState;

	public void setOldRawEntityState(String oldRawEntityState) {
		this.oldRawEntityState = oldRawEntityState;
	}

}
