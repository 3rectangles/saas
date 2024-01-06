/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Component
public class RegexMatchingHelper {

	/**
	 * @param text
	 * @param regex
	 * @return
	 */
	public List<String> getMatchedValuesForRegex(final String text, final String regex) {
		final Pattern patternString = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		final List<String> matchedValues = new ArrayList<>();
		Matcher matcher = patternString.matcher(text);

		while (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {

				if (matcher.group(i) != null) {
					log.info("Match  : " + matcher.group(i));
					matchedValues.add(matcher.group(i).trim());
				}

			}
		}

		if (matchedValues.isEmpty()) {
			log.info("No matches found for text : {} , regex : {}", text, regex);
		}

		return matchedValues;
	}

	/**
	 * @param textWithPlaceholders
	 * @param variableRegexMap
	 * @return
	 */
	public String replaceValues(final String textWithPlaceholders, final Map<String, String> variableRegexMap) {

		final String templatePlaceholderRegex = "\\$\\{(?<placeholder>[A-Za-z0-9-_]+)}";
		final Pattern pattern = Pattern.compile(templatePlaceholderRegex);

		final Matcher matcher = pattern.matcher(textWithPlaceholders);

		final StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			final String key = matcher.group(1);
			final String replacement = variableRegexMap.get(key);
			if (!variableRegexMap.containsKey(key)) {
				throw new IllegalArgumentException(
						"Template contains unmapped key: "
								+ key);
			}
			matcher.appendReplacement(sb, replacement);
		}

		final String resultantRegexString = sb.toString();
		return resultantRegexString;
	}

}
