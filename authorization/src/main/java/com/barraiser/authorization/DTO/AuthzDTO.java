/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.DTO;

import com.barraiser.commons.auth.AuthorizationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthzDTO {

	private Boolean isSuperUser; // At a global or partner level the user has all the access.

	private AuthorizationResult authorizationResult;
}
