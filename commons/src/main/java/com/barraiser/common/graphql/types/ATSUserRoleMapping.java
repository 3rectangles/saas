/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSUserRoleMapping {
	private String id;
	private Role brUserRole;
	private String atsUserRoleId;
	private String atsUserRoleName;
}
