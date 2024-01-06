/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDetailsDTO {

	final String email;

	final String phone;

	final String firstName;

	final String lastName;

	final String partnerId; // Has to be deprecated later.
}
