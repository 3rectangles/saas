/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.files;

import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.barraiser.common.files.FileDownloadService;
import com.barraiser.onboarding.dal.DocumentDAO;
import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.common.graphql.types.Document;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Service
@AllArgsConstructor
public class FileManagementService {
	public static final String CUSTOMER_UPLOADS_S3_BUCKET = "customer-uploads";
	public static final String CERTIFICATE_UPLOADS_S3_BUCKET = "barraiser-certificates";

	public static final String AWS_URL_PREFIX = ".s3.ap-south-1.amazonaws.com/";
	private final TransferManager transferManager;
	private final DocumentRepository documentRepository;
	private final FileDownloadService fileDownloader;

	public String save(final String userId, final String fileUrl, final String type) throws Exception {
		final Path filePath = this.fileDownloader.downloadFile(fileUrl, type);
		final String fileSuffix = this.getFileSuffix(filePath.toFile().getName());
		final String fileName = UUID.randomUUID().toString();
		final Document document = this.saveFile(userId, filePath.toFile(), fileSuffix, fileName);
		log.info("Uploaded the download file to s3 documentID: {}", document.getId());

		return document.getId();
	}

	public Document save(final String userId, final MultipartFile multipartFile)
			throws IOException, InterruptedException {

		final File file = File.createTempFile("upload", ".json");
		multipartFile.transferTo(file);

		final String fileSuffix = this.getFileSuffix(Objects.requireNonNull(multipartFile.getOriginalFilename()));
		final String fileOriginalName = multipartFile.getOriginalFilename();
		return this.saveFile(userId, file, fileSuffix, fileOriginalName);
	}

	public Document saveFile(
			final String userId,
			final File file,
			final String fileSuffix,
			final String fileOriginalName)
			throws InterruptedException {
		final String documentId = UUID.randomUUID().toString();

		final ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("application/" + fileSuffix);
		final PutObjectRequest putObjectRequest = new PutObjectRequest(
				CUSTOMER_UPLOADS_S3_BUCKET,
				String.format("%s.%s", documentId, fileSuffix),
				file)
						.withMetadata(objectMetadata)
						.withTagging(
								new ObjectTagging(
										new ArrayList<>() {
											{
												this.add(new Tag("public", "yes"));
											}
										}))
						.withCannedAcl(CannedAccessControlList.PublicRead);

		final Upload upload = this.transferManager.upload(putObjectRequest);
		final UploadResult result = upload.waitForUploadResult();

		final String fileUrl = "https://" + CUSTOMER_UPLOADS_S3_BUCKET + AWS_URL_PREFIX + result.getKey();

		this.documentRepository.save(
				DocumentDAO.builder()
						.documentId(documentId)
						.uploadedBy(userId)
						.fileUrl(fileUrl)
						.fileName(fileOriginalName)
						.s3Url(String.format("%s/%s", result.getBucketName(), result.getKey()))
						.build());

		return Document.builder()
				.id(documentId)
				.url(fileUrl)
				.fileName(fileOriginalName)
				.build();
	}

	public String saveImage(final File file, String certificateId) throws InterruptedException {
		final ObjectMetadata objectMetadata = new ObjectMetadata();
		String fileSuffix = certificateId + ".png";
		objectMetadata.setContentType("application/" + fileSuffix);
		final PutObjectRequest putObjectRequest = new PutObjectRequest(
				CERTIFICATE_UPLOADS_S3_BUCKET, String.format(fileSuffix), file)
						.withTagging(
								new ObjectTagging(
										new ArrayList<>() {
											{
												this.add(new Tag("public", "yes"));
											}
										}))
						.withCannedAcl(CannedAccessControlList.PublicRead);
		;

		final Upload upload = this.transferManager.upload(putObjectRequest);
		final UploadResult result = upload.waitForUploadResult();

		final String fileUrl = String.format("https://%s%s%s", CERTIFICATE_UPLOADS_S3_BUCKET, AWS_URL_PREFIX,
				result.getKey());

		return fileUrl;
	}

	private String getFileSuffix(final String fileName) {
		final int lastIndexOfDot = fileName.lastIndexOf('.');
		return fileName.substring(lastIndexOfDot + 1);
	}
}
