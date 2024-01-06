package com.barraiser.common.graphql.types;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewCategory {
    private String id;
    private String name;
    List<InterviewCategory> subCategories;
}
