/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class UrlUtil {
	public static String encodeUrl(final String urlString) {
		try {
			URL url = new URL(URLDecoder.decode(urlString, StandardCharsets.UTF_8));
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
			return uri.toASCIIString();
		} catch (Exception e) {
			throw new IllegalArgumentException("URL cannot be encoded: " + urlString);
		}
	}

	public static String getDecodedURL(String encodedString) {
		try {
			return URLDecoder.decode(encodedString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
