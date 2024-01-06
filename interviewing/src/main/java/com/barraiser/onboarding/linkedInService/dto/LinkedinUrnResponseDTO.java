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

public class LinkedinUrnResponseDTO {

    @JsonProperty("localizedLastName")
    private String localizedLastName;

    @JsonProperty("profilePicture")
    private ProfilePicture profilePicture;

    @JsonProperty("firstName")
    private FirstName firstName;

    @JsonProperty("lastName")
    private LastName lastName;

    @JsonProperty("id")
    private String id;

    @JsonProperty("localizedFirstName")
    private String localizedFirstName;



    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class ProfilePicture {

        @JsonProperty("displayImage")
        private String displayImage;


    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class FirstName {

        @JsonProperty("localized")
        private Localized localized;

        @JsonProperty("preferredLocale")
        private PreferredLocale preferredLocale;


    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class LastName {

        @JsonProperty("localized")
        private Localized localized;

        @JsonProperty("preferredLocale")
        private PreferredLocale preferredLocale;


    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Localized {

        @JsonProperty("en_US")
        private String enUs;


    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class PreferredLocale {

        @JsonProperty("country")
        private String country;

        @JsonProperty("language")
        private String language;


    }

}
