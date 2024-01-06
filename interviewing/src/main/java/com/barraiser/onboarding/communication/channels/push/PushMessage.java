package com.barraiser.onboarding.communication.channels.push;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class PushMessage {
    public static final String EXPERT_COMMUNICATION_PROJECT_ID = "d0ed69ae878a4c76a435772fa3b0e185";
    private final AmazonPinpoint amazonPinpoint;

    public SendUsersMessagesResult push(final String userId, final DeviceType deviceType,
                                        final String title, final String body) {
        if (DeviceType.ANDROID == deviceType) {
            return this.pushToAndroid(EXPERT_COMMUNICATION_PROJECT_ID, userId, body, title);
        }
        return null;
    }

    private SendUsersMessagesResult pushToAndroid(final String applicationId,
                                                  final String userId,
                                                  final String body,
                                                  final String title) {

        return this.amazonPinpoint.sendUsersMessages(new SendUsersMessagesRequest()
            .withApplicationId(applicationId)
            .withSendUsersMessageRequest(new SendUsersMessageRequest()
                .withUsers(Map.of(userId, new EndpointSendConfiguration()))
                .withMessageConfiguration(new DirectMessageConfiguration()
                    .withGCMMessage(new GCMMessage()
                        .withBody(body)
                        .withTitle(title)
                    )
                )
            )
        );
    }
}
