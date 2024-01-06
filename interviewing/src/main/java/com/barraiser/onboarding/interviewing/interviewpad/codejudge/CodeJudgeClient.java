package com.barraiser.onboarding.interviewing.interviewpad.codejudge;

import feign.Headers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "code-judge-client", url = CodeJudgeClient.CODE_JUDGE_API_BASE_URL)
public interface CodeJudgeClient {

    String CODE_JUDGE_API_BASE_URL = "https://work.codejudge.io/api/v1";

    @PostMapping(
            value = "/interview/bulk-create/",
            headers = {"Content-Type=application/json", "Accept=application/json"})
    @Headers(value = "Content-Type: application/json, Accept: application/json")
    ResponseEntity<CodeJudgeBulkCreateResponse> bulkGeneratePads(
            @RequestHeader("ent-header") String entHeader,
            @RequestBody CodeJudgeBulkCreateRequest request);
}
