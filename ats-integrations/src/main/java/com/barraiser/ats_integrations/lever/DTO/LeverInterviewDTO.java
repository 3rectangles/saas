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
public class LeverInterviewDTO {

	private String id;

	private String panel;

	private String subject;

	private String note;

	private List<Interviewer> interviewers;

	private String timezone;

	private Long createdAt;

	private String date;

	private Integer duration;

	private String location;

	private String feedbackTemplate;

	private List<String> feedbackForms;

	private String feedbackReminder;

	private String user;

	private String stage;

	private String canceledAt;

	private List<String> postings;

	@Data
	public static class Interviewer {
		private String email;

		private String id;

		private String name;
	}
}
