package com.barraiser.onboarding.cms.pages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ReminderEmailPage {
    @JsonProperty("email_content")
    private String body;

    private String subject;
    private String title;
}
