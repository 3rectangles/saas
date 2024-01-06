/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@FeignClient(name = "messagebird-client-v2", url = "feignUrl")
public interface MessagebirdFeignClient {

	@PostMapping()
	@Headers(value = "Content-Type: application/json")
	void startIVRFlow(URI baseUrl, @RequestBody ObjectNode updateBody);
}
