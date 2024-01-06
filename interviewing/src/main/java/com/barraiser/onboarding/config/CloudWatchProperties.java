package com.barraiser.onboarding.config;

import lombok.Data;
import org.springframework.boot.actuate.autoconfigure.metrics.export.properties.StepRegistryProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "management.metrics.export.cloudwatch")
@Data
public class CloudWatchProperties extends StepRegistryProperties {
    private String namespace;
    private boolean enabled = true;
    private final int numThreads = 10;
}
