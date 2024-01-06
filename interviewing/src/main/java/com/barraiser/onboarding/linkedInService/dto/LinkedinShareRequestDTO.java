package com.barraiser.onboarding.linkedInService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

public class LinkedinShareRequestDTO {

    private String grant_type;

    private String code;

    private String redirect_uri;

    private String client_id;

    private String client_secret;

}
