/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.Resource;

public interface ResourceAuthorizer {
	AuthorizationResult authorize(AuthorizationInput authorizationInput);

	Action action();

	Resource resource();
}
