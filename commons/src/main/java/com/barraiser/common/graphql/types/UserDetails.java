/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetails {
	private String id;
	private String userName;
	private String email;
	private String isdCode;
	private String phone;
	private List<String> roles;
	private String role;
	private Boolean userDetailsPresent;
	private String firstName;
	private String lastName;
	private String almaMater;
	private String currentCompanyName;
	private Integer workExperienceInMonths;
	private List<Company> lastCompanies;
	private String resumeUrl;
	private String category;
	private String timezone;
	private String whatsappNumber;
	private String partnershipModelId; // Will be populated only when we fetch data for a user wrt a partner
}
