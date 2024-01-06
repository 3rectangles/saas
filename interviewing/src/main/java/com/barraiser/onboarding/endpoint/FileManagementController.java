/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.pojo.AuthTokens;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.FileManagementUtil;
import com.barraiser.onboarding.files.FileManagementService;
import com.barraiser.common.graphql.types.Document;
import lombok.AllArgsConstructor;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = {
		"http://localhost",
		"http://localhost:3000",
		"http://staging.barraiser.in",
		"https://staging.barraiser.in",
		"https://staging.barraiser.com",
		"http://staging.barraiser.com",
		"https://barraiser.com",
		"http://barraiser.com",
		"http://barraiser.in",
		"https://barraiser.in"
}, allowCredentials = "true", allowedHeaders = "*")
public class FileManagementController {
	private final FileManagementService fileManagementService;

	@PostMapping(path = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Document> uploadFile(@RequestParam("file") final MultipartFile multipartFile,
			@RequestAttribute(name = "loggedInUser") AuthenticatedUser user,
			@CookieValue(name = AuthTokens.ID_TOKEN, required = false) final String idToken)
			throws IOException, InterruptedException, InvalidJwtException {

		if (user == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		FileManagementUtil.validateFileFormatSupport(multipartFile.getContentType());

		return ResponseEntity.ok().body(this.fileManagementService.save(user.getUserName(), multipartFile));
	}

	@PostMapping(path = "/upload-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Document> uploadResume(
			@RequestPart final MultipartFile multipartFile)
			throws IOException, InterruptedException {
		return ResponseEntity
				.ok()
				.body(this.fileManagementService
						.save(
								UUID.randomUUID().toString(),
								multipartFile));
	}
}
