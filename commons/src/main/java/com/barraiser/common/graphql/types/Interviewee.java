/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Interviewee {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String designation;
	private String linkedInProfile;
	private List<String> lastCompaniesId;
	private String almaMater;
	private Integer workExperienceInMonths;
	private String currentCompanyName;
	private String redactedResumeUrl;
	private String timezone;
	private String phone;
	private String whatsappNumber;
	private String magicToken;
	private String resumeUrl;
}
