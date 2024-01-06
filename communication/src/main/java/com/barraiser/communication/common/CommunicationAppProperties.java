package com.barraiser.communication.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "communication")
public class CommunicationAppProperties {
    private List<String> calendarAccounts;
}
