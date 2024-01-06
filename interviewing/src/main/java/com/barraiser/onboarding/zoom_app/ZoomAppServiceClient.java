/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom_app;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "zoom-app-service", configuration = ZoomAppServiceClientConfig.class)
public interface ZoomAppServiceClient {
	@GetMapping("/api/zoomapp/deeplink")
	ResponseEntity<String> getDeepLink(final ZoomDeepLinkRequest request);
}
