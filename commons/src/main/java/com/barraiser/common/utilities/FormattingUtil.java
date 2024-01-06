/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FormattingUtil {

	public List<String> convertStringToList(final String stringToBeSplit, final String delimiter) {
		List<String> result = new ArrayList<>();

		if (stringToBeSplit != null) {
			result = Arrays.stream(stringToBeSplit.split(delimiter))
					.map(String::strip)
					.collect(Collectors.toList());
		}

		return result;
	}

}
