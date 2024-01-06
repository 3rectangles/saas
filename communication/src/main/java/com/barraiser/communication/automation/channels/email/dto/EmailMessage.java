package com.barraiser.communication.automation.channels.email.dto;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class EmailMessage {
    private final String subject;

    private final String header;

    private final String body;
}
