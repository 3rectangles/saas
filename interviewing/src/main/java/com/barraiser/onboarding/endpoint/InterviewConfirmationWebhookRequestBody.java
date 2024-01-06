package com.barraiser.onboarding.endpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InterviewConfirmationWebhookRequestBody {
    /** true => confirmed by candidate false => cancelled by candidate */
    Boolean rsvp;

    String channel;

    String source;
}
