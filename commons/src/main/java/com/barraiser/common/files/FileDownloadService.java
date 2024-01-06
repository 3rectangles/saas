/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.files;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@AllArgsConstructor
public class FileDownloadService {
	private static final String LEVER = "LEVER";
	private static final String GREENHOUSE = "GREENHOUSE";
	private static final String SMART_RECRUITERS = "SMART_RECRUITERS";
	private static final String MERGE = "MERGE";
	private static final String MERGE_LEVER = "MERGE_lever";
	private final String INVALID_FILE_URL_RESPONSE = "Resume Link is invalid. Please recheck the link and try again. ";
	private final String DOWNLOAD_FILE_ERROR_RESPONSE = "Could not download the file. Please recheck the link and try again. ";
	private final String GOOGLE_DOMAIN = "google.com";

	public String downloadFileAsStringInMemory(final String fileUrl) throws IOException {
		try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream())) {
			String downloadedFile = "";
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				downloadedFile += new String(dataBuffer, 0, bytesRead);
			}
			return downloadedFile;
		} catch (final Exception e) {
			log.error("Could not download file : " + fileUrl);
			throw e;
		}
	}

	public Path downloadFile(String fileUrl, final String type) throws Exception {
		URL url = validateURL(fileUrl);

		if (url.getHost().contains(GOOGLE_DOMAIN))
			url = reconstructGoogleDriveURL(url);

		String fileName;
		if (type == null) {
			fileName = getFileName(url);
		} else if (type.equals(GREENHOUSE)) {
			String s = url.toString();
			fileName = s.substring(s.indexOf("original/") + 9, s.indexOf("?"));
		} else if (type.equals(MERGE) || type.equals(LEVER) || type.equals(SMART_RECRUITERS)) {
			String s = url.toString();
			fileName = s.substring(s.lastIndexOf('/') + 1);
		} else {
			fileName = getFileName(url);
		}

		final Path filePath = Files.createTempFile(getFilePrefix(fileName), '.' + getFileSuffix(fileName));
		try (final InputStream in = url.openStream()) {
			long bytesDownloaded = Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
			log.info("Candidate Resume downloaded: {} bytes", bytesDownloaded);
		} catch (IOException e) {
			throw new Exception(DOWNLOAD_FILE_ERROR_RESPONSE + e.getMessage());
		}
		return filePath;
	}

	/** Returns the direct download URL for gdrive and google docs URLS. */
	private URL reconstructGoogleDriveURL(URL url) throws Exception {
		final String GOOGLE_DRIVE_HOST = "drive.google.com";
		final String GOOGLE_DOCS_HOST = "docs.google.com";
		final String GOOGLE_DOMAIN = "google.com";
		final String GDRIVE_FORMAT_STRING = "https://drive.google.com/uc?id=%s&export=download";
		final String GDOCS_FORMAT_STRING = "https://docs.google.com/document/d/%s/export?format=pdf";

		if (url.getHost().contains(GOOGLE_DOMAIN) && !url.toString().contains("export")) {
			final String fileId = getGoogleFileId(url);
			String newUrl = null;
			if (Objects.equals(url.getHost(), GOOGLE_DRIVE_HOST))
				newUrl = String.format(GDRIVE_FORMAT_STRING, fileId);
			else if (Objects.equals(url.getHost(), GOOGLE_DOCS_HOST))
				newUrl = String.format(GDOCS_FORMAT_STRING, fileId);
			url = new URL(newUrl);
		}
		return url;
	}

	private String getGoogleFileId(URL url) throws Exception {
		final String regex = "/([-\\w]{25,})/";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(url.toString());
		matcher.find();

		try {
			return matcher.group(1);
		} catch (Exception e) {
			throw new Exception(INVALID_FILE_URL_RESPONSE + e.getMessage());
		}
	}

	private URL validateURL(String fileUrl) throws Exception {
		try {
			return new URL(fileUrl);
		} catch (MalformedURLException e) {
			throw new Exception(INVALID_FILE_URL_RESPONSE + e.getMessage());
		}
	}

	private String getFileName(URL url) throws Exception {
		final URLConnection connection = url.openConnection();
		final String fieldValue = connection.getHeaderField("Content-Disposition");
		if (fieldValue == null || !fieldValue.contains("filename=\"")) {
			throw new Exception(INVALID_FILE_URL_RESPONSE);
		}
		final String regex = "filename=\"(.*)\"";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(fieldValue);
		matcher.find();
		try {
			return matcher.group(1);
		} catch (Exception e) {
			throw new Exception(INVALID_FILE_URL_RESPONSE);
		}
	}

	private String getFileSuffix(final String fileName) {
		final int lastIndexOfDot = fileName.lastIndexOf('.');
		return fileName.substring(lastIndexOfDot + 1);
	}

	private String getFilePrefix(final String fileName) {
		final int lastIndexOfDot = fileName.lastIndexOf('.');
		return fileName.substring(0, lastIndexOfDot);
	}
}
