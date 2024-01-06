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
public class PartnerRepDetails {
	private String atsPartnerRepId;
	private String partnerId;
	private String companyId;
	private UserDetails userDetails;
	private Long accessGrantedOn;
	private List<String> teams;
	private List<String> locations;
	private List<String> roles;
}
