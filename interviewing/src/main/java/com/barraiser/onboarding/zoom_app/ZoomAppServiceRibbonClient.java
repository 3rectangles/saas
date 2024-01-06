/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom_app;

import com.barraiser.commons.service_discovery.BarRaiserRibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = "zoom-app-service", configuration = BarRaiserRibbonConfiguration.class)
public class ZoomAppServiceRibbonClient {
}
