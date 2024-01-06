package com.barraiser.onboarding.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
// TODO: remove
public class CopyInterviewVideoControlled {
    private final CopyInterviewVideoService service;

    @PostMapping(path = "/copy-interview-video", consumes = "application/json")
    public void copy(@RequestBody final Map<String, String> input) {
        this.service.copyInterviewVideo(input.get("fromInterviewId"), input.get("toInterviewId"));
    }
}
