/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class GenerateHighlightsEvent {
	@JsonProperty("interview_id")
	private String interviewId;

	@JsonProperty("transcription_s3_path")
	private String transcriptionS3Path;

	@JsonProperty("interview_details")
	private InterviewDetails interviewDetails;// TODO

	@JsonProperty("callback_url")
	private String callbackUrl;
}

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
class InterviewDetails {
	@JsonProperty("interviewer_name")
	private User interviewerName;

	@JsonProperty("interviewee_name")
	private User intervieweeName;

	// TODO: Add Interview and Interview_Flow_Details
}

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
class User {
	@JsonProperty("first")
	private String first;

	@JsonProperty("last")
	private String last;

	@JsonProperty("full")
	private String full;

	@JsonProperty("email")
	private String email;
}
