package com.barraiser.media_management.common;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MediaManagementStaticAppConfigValues {
    @Value("${queue.video-transcoding-completion}")
    private String videoTranscodingCompletionQueue;

    @Value("${queue.video-events}")
    private String videoEventsQueue;

    @Value("${media-serving.domain}")
    private String mediaServingDomain;

    @Value("${queue.media-transcripts-consumer}")
    private String transcriptEventsQueue;
}
