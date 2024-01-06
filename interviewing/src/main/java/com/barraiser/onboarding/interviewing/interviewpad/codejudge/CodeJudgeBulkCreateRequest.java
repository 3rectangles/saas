package com.barraiser.onboarding.interviewing.interviewpad.codejudge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeJudgeBulkCreateRequest {
    @JsonProperty("num_pads")
    private int numPads;

    @JsonProperty("infinite_duration")
    private boolean infiniteDuration;

    @JsonProperty("is_av_req")
    private boolean isAvReq;

    @JsonProperty("show_chat")
    private boolean showChat;

    @JsonProperty("hide_ques_panel")
    private boolean hideQuesPanel;

    @JsonProperty("hide_scorecard")
    private boolean hideScorecard;

    @JsonProperty("hide_waiting_room")
    private boolean hideWaitingRoom;
}
