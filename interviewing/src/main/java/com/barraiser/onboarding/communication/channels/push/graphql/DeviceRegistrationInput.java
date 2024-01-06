package com.barraiser.onboarding.communication.channels.push.graphql;

import com.barraiser.onboarding.communication.channels.push.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeviceRegistrationInput {
    private String deviceToken;
    private Boolean enabled;
    private String scope;
    private DeviceType deviceType;
}
