/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.dal;

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
public class AuditableBaseModel extends BaseModel {

	private String operatedBy;

}
