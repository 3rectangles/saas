package com.barraiser.onboarding.cms.pages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CandidateCompensationConstants {

    @JsonProperty("constants")
    private List<Constants> constants;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Constants {
        @JsonProperty("domain_id")
        private String domainId;

        @JsonProperty("slope")
        private Double slope;

        @JsonProperty("constant")
        private Double constant;
    }
}
