package com.barraiser.onboarding.communication.client;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "messagebird-client", url = "${interview.lifecycle.messagebird.ivr.url}")
public interface MessagebirdFeignClient {

    @PostMapping()
    @Headers(value = "Content-Type: application/json")
    void startIVRFlow(@RequestBody ObjectNode updateBody);
}

