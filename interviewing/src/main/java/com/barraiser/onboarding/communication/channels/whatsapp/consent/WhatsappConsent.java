package com.barraiser.onboarding.communication.channels.whatsapp.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WhatsappConsent {
    private String phone;
    private Boolean consent;
    private Boolean submitted;
}
