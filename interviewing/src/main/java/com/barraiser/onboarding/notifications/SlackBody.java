package com.barraiser.onboarding.notifications;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SlackBody {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
}
