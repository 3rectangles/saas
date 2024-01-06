/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class MustacheFormattingUtil {

	public String formatDataToText(final String template, final Map<String, String> data) throws IOException {
		final DefaultMustacheFactory mf = new DefaultMustacheFactory();
		final Mustache m = mf.compile(String.format("template/%s.mustache", template));
		final StringWriter writer = new StringWriter();
		m.execute(writer, data).flush();
		return writer.toString();
	}

	public String formatObjectDataToText(final String template, final Map<String, Object> data) throws IOException {
		final DefaultMustacheFactory mf = new DefaultMustacheFactory();
		String formatedString = String.format("template/%s.mustache", template);
		Mustache m = mf.compile(formatedString);

		StringWriter writer = new StringWriter();
		m.execute(writer, data).flush();

		m = mf.compile(new StringReader(writer.toString()), formatedString);

		writer = new StringWriter();
		m.execute(writer, data).flush();
		return writer.toString();
	}

}
