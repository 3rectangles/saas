/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PartnerAccessInput {
	private String userId;
	private String partnerId;
	private String email;
	private String phone;
	private List<String> teams;
	private List<String> locations;
	private List<String> roles;
	private String firstName;
	private String lastName;
	private String creationSource;
	private String creationSourceMeta;
}
