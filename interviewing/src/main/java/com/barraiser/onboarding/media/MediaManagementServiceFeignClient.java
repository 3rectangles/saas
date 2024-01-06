package com.barraiser.onboarding.media;

import com.barraiser.common.model.Media;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "media-management-service-feign-client", url = "http://localhost:5000")
public interface MediaManagementServiceFeignClient {

    String SERVICE_CONTEXT_PATH = "media-management";

    @GetMapping(value = SERVICE_CONTEXT_PATH + "/interview/{interviewId}/media")
    @Headers(value = "Content-Type: application/json")
    List<Media> getInterviewMedia(@PathVariable("interviewId") String interviewId);
}

