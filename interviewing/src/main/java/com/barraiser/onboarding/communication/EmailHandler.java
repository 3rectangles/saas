package com.barraiser.onboarding.communication;

import com.barraiser.onboarding.communication.channels.email.EmailEvent;

public interface EmailHandler {
    String objective();

    void process(EmailEvent emailEvent);

    String subject();
}
