/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_login_activity")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLoginActivityDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "email_id")
	private String emailId;

	/**
	 * like OTP , PASSWORD etc.
	 */
	@Column(name = "login_key_type")
	private String loginKeyType;

	@Column(name = "login_key")
	private String loginKey;

	@Column(name = "is_login_attempt_successful")
	private Boolean isLoginAttemptSuccessful;

}
