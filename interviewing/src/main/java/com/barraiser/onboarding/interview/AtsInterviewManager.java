/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.ats_integrations.dto.PostATSNoteDTO;
import com.barraiser.onboarding.ATSFeignClient;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.pojo.PostInterviewCompletionNoteData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class AtsInterviewManager {
	private final UserDetailsRepository userDetailsRepository;
	private final ATSFeignClient atsFeignClient;

	private static final String ATS_INTERVIEW_COMPLETION_NOTE = "Interview completed by %s, round - %s \n Report link - https://app.barraiser.com/candidate-evaluation/%s/evaluation-round/%s";

	public void postInterviewCompletionNote(final PostInterviewCompletionNoteData postInterviewCompletionNoteData) {
		final UserDetailsDAO userDetails = this.userDetailsRepository
				.findById(postInterviewCompletionNoteData.getInterviewerId()).get();
		final String interviewerName = String.format("%s %s",
				Objects.requireNonNullElse(userDetails.getFirstName(), ""),
				Objects.requireNonNullElse(userDetails.getLastName(), ""));

		this.atsFeignClient.postNote(PostATSNoteDTO.builder()
				.partnerId(postInterviewCompletionNoteData.getPartnerId())
				.evaluationId(postInterviewCompletionNoteData.getEvaluationId())
				.message(String.format(
						ATS_INTERVIEW_COMPLETION_NOTE,
						interviewerName, postInterviewCompletionNoteData.getInterviewRound(),
						postInterviewCompletionNoteData.getEvaluationId(),
						postInterviewCompletionNoteData.getInterviewId()))
				.build());
	}
}
