/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.BarRaiserClient;
import com.barraiser.ats_integrations.lever.DTO.ResumeDTO;
import com.barraiser.ats_integrations.merge.DTO.PassthroughInputDTO;
import com.barraiser.ats_integrations.merge.DTO.PassthroughNoDataResponseDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.common.graphql.types.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

@Component
@Log4j2
@AllArgsConstructor
public class MergeLeverResumeDownloader {
	private static final String DOWNLOAD_FILE_ERROR_RESPONSE = "Could not download the file. Please recheck the link and try again. ";

	private static final String RESUME_DOWNLOAD_PATH_STRING_FORMAT = "/opportunities/%s/resumes/%s/download";
	private static final String RESUME_FETCH_ERROR_RESPONSE_STRING_FORMAT = "Unable to download resume from lever OpportunityId : %s";
	private static final String APPLICATION_PDF_CONTENT_TYPE = "application/pdf";
	private static final String FILE = "file";
	private static final String GET_METHOD = "GET";

	private final BarRaiserClient barRaiserClient;
	private final MergeATSClient mergeATSClient;
	private final ObjectMapper objectMapper;

	public Document getResumeFile(final String opportunityId, final ResumeDTO resumeDTO, final String authHeader,
			final String token) throws Exception {
		final Path resumePath = Files.createTempFile(
				UUID.randomUUID().toString(),
				resumeDTO.getFile().getExt());

		final PassthroughNoDataResponseDTO response = this.downloadResumeFile(opportunityId,
				resumeDTO.getId(), authHeader, token);

		try {
			// Converting file
			byte[] decodedBytes = Base64.getDecoder()
					.decode(objectMapper.convertValue(response.getResponse(), String.class));
			InputStream decompressedStream = decompressGzip(decodedBytes);
			Files.copy(decompressedStream, resumePath, StandardCopyOption.REPLACE_EXISTING);
			decompressedStream.close();

		} catch (IOException exception) {
			throw new Exception(DOWNLOAD_FILE_ERROR_RESPONSE + exception.getMessage());
		}

		File resumeFile = resumePath.toFile();
		FileInputStream input = new FileInputStream(resumeFile);
		MultipartFile multipartFile = new MockMultipartFile(

				FILE, resumeFile.getName(), APPLICATION_PDF_CONTENT_TYPE, IOUtils.toByteArray(input));
		return this.barRaiserClient.uploadResume(multipartFile).getBody();
	}

	private PassthroughNoDataResponseDTO downloadResumeFile(final String opportunityId, final String resumeId,

			final String authHeader, final String token) {
		try {
			return this.mergeATSClient.callPassthroughNoData(
					authHeader,
					token,
					PassthroughInputDTO.builder()

							.method(GET_METHOD)
							.path(String.format(RESUME_DOWNLOAD_PATH_STRING_FORMAT, opportunityId, resumeId))
							.build())
					.getBody();

		} catch (Exception exception) {
			log.warn(

					String.format(RESUME_FETCH_ERROR_RESPONSE_STRING_FORMAT, opportunityId),
					exception);

			throw exception;
		}
	}

	public static InputStream decompressGzip(byte[] compressedData) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
		return new GZIPInputStream(byteArrayInputStream);
	}
}
