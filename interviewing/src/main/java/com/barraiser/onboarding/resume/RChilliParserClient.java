package com.barraiser.onboarding.resume;

import com.barraiser.onboarding.resume.dto.ParseResumeRequestDTO;
import com.barraiser.onboarding.resume.dto.ParseResumeResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rchilli-parser-client", url="https://rest.rchilli.com/RChilliParser/Rchilli/parseResume", configuration = RChilliConfig.class)
public interface RChilliParserClient {
    @PostMapping
    ParseResumeResponseDTO parseResume(@RequestBody ParseResumeRequestDTO parseResumeRequest);

    @PostMapping
    String parseResumeToJsonString(@RequestBody ParseResumeRequestDTO parseResumeRequest);
}
