package com.barraiser.media_management.controller;

import com.barraiser.common.model.Media;
import com.barraiser.media_management.service.MediaManagementService;
import com.barraiser.media_management.transcript.TranscriptFetcher;
import com.barraiser.common.model.TranscriptDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
public class MediaManagementController {
    final static String SERVICE_CONTEXT_PATH = "media-management";
    final static String ENTITY_TYPE_INTERVIEW = "INTERVIEW";

    final MediaManagementService mediaManagementService;
    private final TranscriptFetcher transcriptFetcher;


    @GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{entityId}/media")
    public List<Media> getInterviewMedia(@PathVariable("entityId") final String entityId) {
        return this.mediaManagementService.getMedia(ENTITY_TYPE_INTERVIEW, entityId);
    }

    @GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{entityId}/transcript")
    public TranscriptDTO getTranscript(@PathVariable("entityId") final String entityId) throws IOException {
        return this.transcriptFetcher.getTranscript(entityId);
    }
}
