package com.barraiser.communication.message;

import com.barraiser.communication.pojo.SlackMessage;
import com.barraiser.communication.pojo.SlackMessageBody;

import feign.Headers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "slack-message-posting-feign-client", url = "https://slack.com/api")
public interface SlackMessageFeignClient {
    @PostMapping(value = "/chat.postMessage")
    @Headers(value = "Content-Type: application/json")
    SlackMessage sendMessage(
            @RequestBody SlackMessageBody slackMessageBody,
            @RequestHeader("Authorization") String authHeader);
}
