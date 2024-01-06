/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.BarRaiserClient;
import com.barraiser.common.graphql.types.Document;
import com.barraiser.ats_integrations.lever.DTO.ResumeDTO;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class LeverResumeDownloader {
	private static final String DOWNLOAD_FILE_ERROR_RESPONSE = "Could not download the file. Please recheck the link and try again. ";

	private final LeverAccessManager leverAccessManager;
	private final LeverClient leverClient;
	private final BarRaiserClient barRaiserClient;

	public Document getResumeFile(final String partnerId, final ResumeDTO resumeDTO) throws Exception {
		final Path resumePath = Files.createTempFile(
				UUID.randomUUID().toString(),
				resumeDTO.getFile().getExt());

		final Response response = this.downloadResumeFile(
				partnerId,
				resumeDTO.getFile().getDownloadUrl());

		try (final InputStream inputStream = response.body().asInputStream()) {
			Long bytesDownload = Files.copy(
					inputStream,
					resumePath,
					StandardCopyOption.REPLACE_EXISTING);

			log.info(
					"Lever resume downloaded: {} bytes",
					bytesDownload);
		} catch (IOException exception) {
			throw new Exception(DOWNLOAD_FILE_ERROR_RESPONSE + exception.getMessage());
		}

		File resumeFile = resumePath.toFile();
		FileInputStream input = new FileInputStream(resumeFile);
		MultipartFile multipartFile = new MockMultipartFile(
				"file",
				resumeFile.getName(),
				"application/pdf",
				IOUtils.toByteArray(input));

		return this.barRaiserClient.uploadResume(multipartFile).getBody();
	}

	private Response downloadResumeFile(final String partnerId, final String downloadUrl)
			throws Exception {
		try {
			final String authorization = this.leverAccessManager
					.getAuthorization(partnerId);

			return this.leverClient
					.downloadResumeFile(
							new URI(downloadUrl),
							authorization);
		} catch (Exception exception) {
			log.warn(
					String.format(
							"Unable to download resume from lever partnerId %s, downloadUrl : %s",
							partnerId,
							downloadUrl),
					exception);

			throw exception;
		}
	}
}
