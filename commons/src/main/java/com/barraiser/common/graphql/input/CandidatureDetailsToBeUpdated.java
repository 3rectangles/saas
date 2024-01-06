/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.UserDetailsInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CandidatureDetailsToBeUpdated {
	private UserDetailsInput userDetailsInput;
	private String pocEmail;
}
