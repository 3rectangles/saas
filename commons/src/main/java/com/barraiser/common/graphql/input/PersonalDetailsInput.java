package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.CurrentCTC;
import com.barraiser.common.graphql.types.WorkExperience;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PersonalDetailsInput {
    private CurrentCTC currentCTC;
    private WorkExperience workExperience;
    private int workExperienceInMonths;
    private String currentEmployer;
    private String currentEmployerName;
    private String areaOfInterest;
    private String resumeDocumentId;
}
