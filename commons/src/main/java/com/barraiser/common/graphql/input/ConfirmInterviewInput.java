package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.Reason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ConfirmInterviewInput {
    private Boolean candidateConfirmation;

    private Boolean interviewerConfirmation;

    @NotNull(message = "Invalid interview")
    private String interviewId;

    @NotNull(message = "Invalid type")
    private String type;

    @Null(message = "Invalid Communication Channel")
    private String channel;

    private String source;

    private Reason cancellationReason;
}
