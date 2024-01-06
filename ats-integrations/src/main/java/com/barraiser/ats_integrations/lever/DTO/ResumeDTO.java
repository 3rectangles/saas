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
public class ResumeDTO {
	private String id;

	private Long createdAt;

	private File file;

	@Data
	public static class File {
		private String downloadUrl;

		private String ext;

		private String name;

		private Long uploadedAt;

		private String status;

		private Integer size;
	}

	private ParsedData parsedData;

	@Data
	public static class ParsedData {
		private List<Position> positions;

		private List<School> schools;
	}

	@Data
	public static class Position {
		private String org;

		private String title;

		private String summary;

		private String location;

		private YearAndMonth start;

		private YearAndMonth end;
	}

	@Data
	public static class School {
		private String org;

		private String degree;

		private String field;

		private String summary;

		private YearAndMonth start;

		private YearAndMonth end;
	}

	@Data
	public static class YearAndMonth {
		private Integer year;

		private Integer month;
	}
}
