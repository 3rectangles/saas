package com.barraiser.onboarding.config;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsyncClient;
import io.micrometer.cloudwatch.CloudWatchConfig;
import io.micrometer.cloudwatch.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CloudwatchConfig {
    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry(final CloudWatchConfig config,
                                                           final Clock clock, final AmazonCloudWatchAsync client) {
        return new CloudWatchMeterRegistry(config, clock, client);
    }

    @Bean
    public Clock micrometerClock() {
        return Clock.SYSTEM;
    }

    @Bean
    public AmazonCloudWatchAsync amazonCloudWatchAsync() {
        return AmazonCloudWatchAsyncClient.asyncBuilder().build();
    }

    @Bean
    public CloudWatchConfig cloudWatchConfig(final CloudWatchProperties properties) {
        return new CloudWatchConfig() {
            @Override
            public String prefix() {
                return null;
            }

            @Override
            public String namespace() {
                return properties.getNamespace();
            }

            @Override
            public Duration step() {
                return properties.getStep();
            }

            @Override
            public boolean enabled() {
                return properties.isEnabled();
            }

            @Override
            public int numThreads() {
                return properties.getNumThreads();
            }

            @Override
            public int batchSize() {
                return properties.getBatchSize();
            }

            @Override
            public String get(final String s) {
                return null;
            }
        };
    }
}
