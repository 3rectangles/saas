/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetailsInput {
	private String id;
	private String email;
	private String phone;
	private String fullName;
	private String almaMater;
	private String currentCompanyName;
	private Integer workExperienceInMonths;
	private String timezone;
	private String designation;
	private String documentId;
}
