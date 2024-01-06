package com.barraiser.common.monitoring;

import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Currently this is designed with respect to BarRaiser Workspace */
@Log4j2
@Component
@AllArgsConstructor
public class MonitoringSlackChannelService {
    private final MethodsClient methodsClient;

    public void sendMessage(final String subject, final String message, final String channel)
            throws IOException, SlackApiException {
        final String slackMessage = String.format("%s \n ```%s```", subject, message);
        final ChatPostMessageRequest request =
                ChatPostMessageRequest.builder().channel(channel).text(slackMessage).build();
        final ChatPostMessageResponse res = this.methodsClient.chatPostMessage(request);

        if (res.getError() != null) {
            log.info("Problem Sending to slack : " + res.getError());
        }
    }
}
