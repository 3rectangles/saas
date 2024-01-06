package com.barraiser.onboarding.zoom;

import com.barraiser.onboarding.dal.QuestionDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuestionTimeTaggingRequest {
    private String zoomMeetingId;
    private List<QuestionDAO> questions;
    private String interviewerName;
    private String interviewerInitials;
}
