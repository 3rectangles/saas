/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.BarRaiserClient;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.AttachmentDTO;
import com.barraiser.common.graphql.types.Document;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersAttachmentDownloader {
	private static final String DOWNLOAD_FILE_ERROR_RESPONSE = "Could not download the file. Please recheck the link and try again. ";

	private final SmartRecruitersAccessManager smartRecruitersAccessManager;
	private final SmartRecruitersClient smartRecruitersClient;
	private final BarRaiserClient barRaiserClient;

	public Document getAttachment(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final AttachmentDTO attachmentDTO) throws Exception {
		final Path resumePath = Files.createTempFile(
				UUID.randomUUID().toString(),
				attachmentDTO
						.getName()
						.substring(attachmentDTO
								.getName()
								.lastIndexOf('.')));

		final Response response = this.downloadAttachment(
				partnerATSIntegrationDAO,
				attachmentDTO.getId());

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
				attachmentDTO
						.getContentType(),
				IOUtils.toByteArray(input));

		return this.barRaiserClient
				.uploadResume(multipartFile)
				.getBody();
	}

	private Response downloadAttachment(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String attachmentId)
			throws Exception {
		try {
			final String apiKey = this.smartRecruitersAccessManager
					.getApiKey(partnerATSIntegrationDAO);

			return this.smartRecruitersClient
					.downloadAttachment(apiKey, attachmentId);
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to download resume from SR partnerId %s, attachmentId:%s",
							partnerATSIntegrationDAO.getPartnerId(),
							attachmentId),
					exception);

			throw exception;
		}
	}
}
