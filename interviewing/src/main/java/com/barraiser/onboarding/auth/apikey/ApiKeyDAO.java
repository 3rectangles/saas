/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "api_key")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class ApiKeyDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "key")
	private String key;

	@Column(name = "key_name")
	private String keyName;

	@Column(name = "scope")
	private String scope;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "roles")
	private List<String> roles;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "disabled_on")
	private Instant disabledOn;

	@Lob
	@Column(name = "encrypted_key")
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] encryptedKey;
}
