package com.barraiser.onboarding.linkedInService.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

public class LinkedinPostResponseDTO {
    @JsonProperty("owner")
    private String owner;

    @JsonProperty("activity")
    private String activity;
}
