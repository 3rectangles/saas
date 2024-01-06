/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations;

import com.barraiser.common.graphql.types.Document;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "barraiser-client", url = "http://localhost:5000")
public interface BarRaiserClient {
	@PostMapping(path = "/upload-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<Document> uploadResume(
			@RequestPart final MultipartFile multipartFile);
}
