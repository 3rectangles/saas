/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.files.exception;

public class UnsupportedFileFormatException extends RuntimeException {

	public UnsupportedFileFormatException(final String message) {
		super(message);
	}

	public UnsupportedFileFormatException() {
		super("The file format is currently not supported.");
	}
}
