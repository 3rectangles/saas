package com.barraiser.common.graphql.input;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserSignUpInput {
    private String name;
    private String email;
    private String linkedInProfile;
    private String phone;
    private String birthDate;
}
