package com.barraiser.communication.automation.channels.email.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class EmailRecipient {
    private final List<String> toEmails;

    private final List<String> ccEmails;

    private final List<String> bccEmails;
}
