/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostingDTO {
	private String id;

	private String text;

	private Long createdAt;

	private Long updatedAt;

	private String state;

	private List<String> distributionChannels;

	private String confidentiality;

	private String user;

	private String owner;

	private String hiringManager;

	private Category categories;

	private List<String> tags;

	private Content content;

	private List<String> followers;

	private String reqCode;

	private List<String> requisitionCodes;

	private Urls urls;

	@Data
	public static class Category {
		private String team;

		private String department;

		private String location;

		private String commitment;

		private String level;
	}

	@Data
	public static class Content {
		private String description;

		private String descriptionHtml;

		private List<TextAndContent> lists;

		private String closing;

		private String closingHtml;
	}

	@Data
	public static class TextAndContent {
		private String text;

		private String content;
	}

	@Data
	public static class Urls {
		private String list;

		private String show;

		private String apply;
	}
}
