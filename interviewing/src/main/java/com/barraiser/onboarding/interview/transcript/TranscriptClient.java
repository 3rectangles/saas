package com.barraiser.onboarding.interview.transcript;

import com.barraiser.common.model.TranscriptDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "transcript-client", url = "http://localhost:5000")
public interface TranscriptClient {
    final String SERVICE_CONTEXT_PATH = "media-management";

    @GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{entityId}/transcript")
    @Headers(value = "Content-Type: application/json")
    TranscriptDTO getInterviewTranscript(@PathVariable("entityId") String interviewId);
}
