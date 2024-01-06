/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "ats_access_token")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSAccessTokenDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "token")
	private String token;

	@Column(name = "token_type")
	private String tokenType;

	@Column(name = "ats_provider")
	private String atsProvider;

	@Lob
	@Column(name = "encrypted_token")
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] encryptedToken;
}
