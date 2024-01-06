package com.barraiser.onboarding.communication.channels.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent {
    private String objective;
    private String expertId;
}
