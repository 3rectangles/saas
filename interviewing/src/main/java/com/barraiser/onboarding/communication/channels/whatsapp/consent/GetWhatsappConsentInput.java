package com.barraiser.onboarding.communication.channels.whatsapp.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetWhatsappConsentInput {
    private String phone;
}
