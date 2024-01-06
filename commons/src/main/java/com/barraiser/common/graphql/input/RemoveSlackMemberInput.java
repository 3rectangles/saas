package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RemoveSlackMemberInput {
    @Id
    private String channel;
    private String channelId;
    private String partnerId;
}
