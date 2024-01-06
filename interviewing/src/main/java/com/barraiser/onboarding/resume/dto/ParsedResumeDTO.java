package com.barraiser.onboarding.resume.dto;

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
public class ParsedResumeDTO {
    @JsonProperty("ResumeFileName")
    private String fileName;

    @JsonProperty("Name")
    private Name name;

    @JsonProperty("Email")
    private List<Email> emails;

    @JsonProperty("PhoneNumber")
    private List<PhoneNumber> phoneNumbers;

    @JsonProperty("WorkedPeriod")
    private WorkedPeriod workedPeriod;

    @JsonProperty("SegregatedSkill")
    private List<Skill> skills;

    @JsonProperty("Experience")
    private String experience;

    @JsonProperty("SegregatedExperience")
    private List<Employers> employers;

    @JsonProperty("Certification")
    private String certification;

    @JsonProperty("Qualification")
    private String qualification;

    @JsonProperty("CurrentEmployer")
    private String currentEmployer;

    @JsonProperty("Address")
    private List<Address> address;

    @JsonProperty("Achievements")
    private String achievements;

    @JsonProperty("Hobbies")
    private String hobbies;

    @JsonProperty("JobProfile")
    private String jobProfile;

    @JsonProperty("Institution")
    private String institution;

    @JsonProperty("SegregatedQualification")
    private List<SegregatedQualification> segregatedQualifications;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Name {
        @JsonProperty("FullName")
        private String fullName;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Email {
        @JsonProperty("EmailAddress")
        private String emailAddress;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class PhoneNumber {
        @JsonProperty("FormattedNumber")
        private String formattedNumber;

        @JsonProperty("ISDCode")
        private String isdCode;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class WorkedPeriod {
        @JsonProperty("TotalExperienceInMonths")
        private String totalExperienceInMonths;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Skill {
        @JsonProperty("Skill")
        private String skill;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Employers {
        @JsonProperty("Employer")
        private Employer employer;

        @JsonProperty("IsCurrentEmployer")
        private String isCurrentEmployer;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Employer {
        @JsonProperty("EmployerName")
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Address {
        @JsonProperty("FormattedAddress")
        private String fullAddress;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Institution {
        @JsonProperty("Name")
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class SegregatedQualification {
        @JsonProperty("Institution")
        private Institution institution;
    }
}
