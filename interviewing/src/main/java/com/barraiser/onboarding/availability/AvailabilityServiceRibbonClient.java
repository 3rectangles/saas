/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.service_discovery.BarRaiserBackendServicesNames;
import com.barraiser.commons.service_discovery.BarRaiserRibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = BarRaiserBackendServicesNames.AVAILABILITY_SERVICE, configuration = BarRaiserRibbonConfiguration.class)
public class AvailabilityServiceRibbonClient {
}
