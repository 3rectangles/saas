/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.dal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class VersionedEntityId implements Serializable {
	private static final long serialVersionUID = 4752185863075389352L;
	private String id;
	@Column(name = "version_id")
	private Integer version;

	public VersionedEntityId() {
	}

	public VersionedEntityId(final String id, final Integer version) {
		this.id = id;
		this.version = version;
	}

}
