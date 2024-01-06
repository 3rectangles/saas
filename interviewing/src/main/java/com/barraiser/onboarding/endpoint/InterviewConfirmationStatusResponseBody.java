package com.barraiser.onboarding.endpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InterviewConfirmationStatusResponseBody {
    /**
     * 'NOACTION' => no response yet
     * 'CONFIRMED'=> confirmed by candidate
     * 'DENIED' => cancelled by candidate
     */
    String candidateStatus;
}
