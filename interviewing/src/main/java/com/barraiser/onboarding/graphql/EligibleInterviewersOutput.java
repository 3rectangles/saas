/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.common.graphql.types.Interviewer;
import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EligibleInterviewersOutput {
	private List<Interviewer> interviewers;
	private List<Interviewer> availableInterviewers;
	private List<Interviewer> unavailableInterviewers;
	private List<Interviewer> bookedInterviewers;
}
