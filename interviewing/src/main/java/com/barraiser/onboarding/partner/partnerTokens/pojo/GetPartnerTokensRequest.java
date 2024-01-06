package com.barraiser.onboarding.partner.partnerTokens.pojo;

import lombok.*;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetPartnerTokensRequest {
    private String email;
}
