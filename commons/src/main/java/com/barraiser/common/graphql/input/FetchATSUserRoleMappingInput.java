/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FetchATSUserRoleMappingInput {
	private String partnerId;
	private String atsProvider; // TODO : Convert to Enum.
}
