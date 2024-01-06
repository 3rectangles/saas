package com.barraiser.onboarding.zoom;



import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "question-time-feign-client", url = "http://ec2-65-0-177-145.ap-south-1.compute.amazonaws.com")
public interface ZoomTranscriptTimeAnnotatorFeignClient {
    @PostMapping(value ="/question-time/predict")
    @Headers(value = "Content-Type: application/json")
    QuestionTimeTaggingResponse getQuestionStartTimePredicted(@RequestBody QuestionTimeTaggingRequest questionTimeTaggingRequest);
}
