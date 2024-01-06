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

public class LinkedinPostRequestDTO {

    @JsonProperty("author")
    private String author;

    @JsonProperty(" lifecycleState")
    private String  lifecycleState;

    @JsonProperty("specificContent")
    private SpecificContent  specificContent;

    @JsonProperty("visibility")
    private Visibility visibility;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class SpecificContent {

        @JsonProperty("com.linkedin.ugc.ShareContent")
        private ComLinkedinUgcShareContent comlinkedinugcShareContent;

        @JsonProperty("shareMediaCategory")
        private String  shareMediaCategory ="NONE";

    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class ComLinkedinUgcShareContent {

        @JsonProperty("shareCommentary")
       private ShareCommentary shareCommentary;

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class ShareCommentary {
        @JsonProperty("text")
        private String text = "Hello World! This is my  second programatic share!";

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Visibility {

        @JsonProperty("com.linkedin.ugc.MemberNetworkVisibility")
        private String comLinkedinUgcMemberNetworkVisibility = "PUBLIC";


    }




}
