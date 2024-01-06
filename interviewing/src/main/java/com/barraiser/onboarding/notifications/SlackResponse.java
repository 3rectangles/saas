package com.barraiser.onboarding.notifications;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SlackResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("team")
    private Team slackTeam;

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Team{
        @JsonProperty("id")
        private String teamId;
    }

    @JsonProperty("incoming_webhook")
    private Webhook slackWebhook;

    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static  class Webhook{
        @JsonProperty("channel_id")
        private String channelId;

        @JsonProperty("channel")
        private String channel;

        @JsonProperty("url")
        private String webhookUrl;
    }

}
