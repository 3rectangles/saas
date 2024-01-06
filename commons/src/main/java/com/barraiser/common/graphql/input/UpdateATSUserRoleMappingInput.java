/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.ATSUserRoleMapping;
import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateATSUserRoleMappingInput {
	private String partnerId;
	private String atsProvider;
	private List<ATSUserRoleMappingInput> roleMappings;
}
