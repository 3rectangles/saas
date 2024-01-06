/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.transcript;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranscriptUploader {
	public static final String S3_BUCKET = "barraiser-transcripts";

	private final TransferManager transferManager;
	private final ObjectMapper objectMapper;

	public void uploadTranscriptForInterview(final Transcript transcript, final String interviewId) {
		log.info("uploading transcript for interview : " + interviewId);
		try {
			final ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentType("application/json");
			final PutObjectRequest putObjectRequest = new PutObjectRequest(
					S3_BUCKET,
					String.format("%s.%s", interviewId, "json"),
					new ByteArrayInputStream(this.objectMapper.writeValueAsBytes(transcript)),
					objectMetadata);
			this.transferManager.upload(putObjectRequest);
		} catch (final JsonProcessingException e) {
			log.error("Could not upload transcript to s3 for interview : " + interviewId);
			throw new RuntimeException(e);
		}
	}
}
