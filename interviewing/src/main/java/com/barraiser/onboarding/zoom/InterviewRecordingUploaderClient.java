/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.onboarding.zoom.dto.UploadInterviewRecordingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "interview-recording-upload-client", url = "https://hooks.zapier.com/hooks/catch/9537671/bsygyrb/")
public interface InterviewRecordingUploaderClient {
	@PostMapping("")
	void uploadRecording(@RequestBody final UploadInterviewRecordingDTO body);
}
