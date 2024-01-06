/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSUserRoleMappingInput {
	private String id;
	private String brUserRoleId;
	private String atsUserRoleId;
	private String atsUserRoleName;
}
