/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.ApplicableFilterType;
import com.barraiser.common.graphql.types.FieldType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "filter")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class FilterDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "query")
	private String query;

	@Column(name = "filter_context")
	private String filterContext;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "dependant_fields")
	private List<String> dependantFields;

	@Enumerated(EnumType.STRING)
	@Column(name = "field_type")
	private FieldType fieldType;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "name")
	private String name;

	@Enumerated(EnumType.STRING)
	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "operations_possible")
	private List<String> operationsPossible;

	@Enumerated(EnumType.STRING)
	@Column(name = "filter_type")
	private ApplicableFilterType filterType;

	@Enumerated(EnumType.STRING)
	@Column(name = "entity_type")
	private EntityType entityType;

	@Column(name = "query_mapping")
	private String queryMapping;

	@Column(name = "internal_name")
	private String internalName;

	@Column(name = "default_value")
	private String defaultValue;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;
}
