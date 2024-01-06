/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

import com.barraiser.common.requests.QuestionTagRequest;
import com.barraiser.common.responses.QuestionTagResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "question-tag-feign-client", url = "http://3.7.71.167:8080")
public interface QuestionTagFeignClient {

	@PostMapping(value = "/predict")
	@Headers(value = "Content-Type: application/json")
	QuestionTagResponse getQuestionTags(@RequestBody QuestionTagRequest questionBody);

}
