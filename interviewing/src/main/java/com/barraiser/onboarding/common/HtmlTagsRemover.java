/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

public class HtmlTagsRemover {

	public static String removeHtmlTags(final String str) {
		return str == null ? null : str.replaceAll("<.*?>", "");
	}
}
