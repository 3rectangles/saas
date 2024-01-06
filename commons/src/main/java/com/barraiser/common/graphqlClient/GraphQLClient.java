/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphqlClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "graphql-client", url = "http://localhost:5000")
public interface GraphQLClient {
	@PostMapping("/graphql")
	Object getGraphQLData(@RequestBody GraphQLRequestBody requestBody,
			@RequestHeader(value = "Authorization") String apiKey);
}
