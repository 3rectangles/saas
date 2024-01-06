/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import com.barraiser.onboarding.files.exception.UnsupportedFileFormatException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class FileManagementUtil {

	private final static String MIME_TYPE_PDF = "application/pdf";
	private final static String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	/**
	 * @param fileMimeType
	 *            MIME types like application/pdf , image/gif etc.
	 */
	public static void validateFileFormatSupport(final String fileMimeType) {
		final List<String> allowedFileUploadMIMETypes = new ArrayList<String>(
				Arrays.asList(MIME_TYPE_PDF, MIME_TYPE_DOCX));

		log.info("The format of the file is : " + fileMimeType);

		if (!allowedFileUploadMIMETypes.contains(fileMimeType)) {
			throw new UnsupportedFileFormatException();
		}
	}
}
