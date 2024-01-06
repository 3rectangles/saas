package com.barraiser.onboarding.config;

import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class SlackConfigTest {
    @Test
    public void testingAMessage() throws IOException, SlackApiException {
        final SlackConfig config = new SlackConfig();

        // Build a request object
        final ChatPostMessageRequest request = ChatPostMessageRequest.builder()
            .channel("#monitoring") // Use a channel ID `C1234567` is preferrable
            .text(":wave: Hi from a bot written in Java!")
            .build();

        final ChatPostMessageResponse res = config.getMethodClient(null).chatPostMessage(request);
        System.out.println(res.getError());
    }
}
