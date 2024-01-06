package com.barraiser.onboarding.search.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SkillSO {
    @JsonProperty("objectID")
    private String id;
    private String name;
    private String parentId; // A skill could be child of another
    private String fieldId; // Data Science
    private String domain; // Software
}
