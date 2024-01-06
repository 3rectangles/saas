/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.onboarding.files.FileManagementService;
import com.barraiser.onboarding.resume.ResumeParserManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class ResumeUrlProcessor {
	private DocumentRepository documentRepository;
	private FileManagementService fileManagementService;
	private ResumeParserManager resumeParserManager;

	/**
	 * returns s3 resume url, if document id is null then uploads the resume in s3
	 * bucket and returns the url
	 */
	public String getResumeUrl(
			final String userId,
			final String inputDocumentId,
			final String externalResumeLink,
			final String type) throws Exception {
		String documentId;
		String resumeUrl = null;

		if (inputDocumentId == null) {
			documentId = this.getDocumentIdFromExternalResumeLink(userId, externalResumeLink, type);
		} else {
			documentId = inputDocumentId;
		}

		try {
			resumeUrl = this.documentRepository.findById(documentId).get().getFileUrl();
		} catch (final Exception e) {
			log.error(String.format("Resume does not exist for document id %s in documents table", documentId), e);
		}
		return resumeUrl;
	}

	public String getDocumentIdFromExternalResumeLink(final String userId, final String fileUrl, final String type)
			throws Exception {
		String documentId = this.fileManagementService.save(userId, fileUrl, type); // download and store resume in s3
																					// bucket
		this.resumeParserManager.parseAndStoreResume(documentId);
		return documentId;
	}
}
