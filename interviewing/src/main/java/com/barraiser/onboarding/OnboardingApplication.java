/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.barraiser" })
@EntityScan(basePackages = { "com.barraiser" })
@EnableAspectJAutoProxy
@EnableFeignClients(basePackages = { "com.barraiser" })
@EnableJpaRepositories(basePackages = { "com.barraiser" })
public class OnboardingApplication {

	public static void main(final String[] args) {

		SpringApplication.run(OnboardingApplication.class, args);

	}
}
