package com.barraiser.media_management.zoom;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.barraiser.common.constants.ServiceConfigurationConstants.SCHEDULING_CONTEXT_PATH;

@FeignClient(name = "media-client", url = "http://localhost:5000")
public interface MediaClient {

    @GetMapping(value = SCHEDULING_CONTEXT_PATH + "/zoom/{meetingId}/entity")
    String getEntityForZoomId(@PathVariable("meetingId") String meetingId);
}
