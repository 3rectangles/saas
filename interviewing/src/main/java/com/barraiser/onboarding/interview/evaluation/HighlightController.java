/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.types.CreateHighlightsInput;
import com.barraiser.common.graphql.types.Highlight;
import com.barraiser.onboarding.interview.HighlightManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor
public class HighlightController {
	private final HighlightManager highlightManager;

	@PostMapping(value = "/create_highlights", produces = "application/json", consumes = "application/json")
	public List<Highlight> createHighlights(
			@RequestBody final CreateHighlightsInput createHighlightsInput) {
		return highlightManager.saveHighlights(createHighlightsInput);
	}
}
