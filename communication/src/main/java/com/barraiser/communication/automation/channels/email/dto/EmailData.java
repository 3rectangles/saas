package com.barraiser.communication.automation.channels.email.dto;

import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class EmailData {
    private String fromEmail;

    private EmailMessage message;

    private EmailRecipient recipient;

    @JsonIgnore
    private CommunicationInput input;
}
