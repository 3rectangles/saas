/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search.auth;

import com.barraiser.commons.auth.AuthorizationInput;
import graphql.schema.DataFetchingEnvironment;

public interface AuthorizationInputConstructor {

	AuthorizationInput construct(DataFetchingEnvironment dataFetchingEnvironment);

}
