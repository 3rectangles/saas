package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "parsed_resume")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParsedResumeDAO extends BaseModel {

    @Id
    @Column(name = "document_id")
    private String documentId;

    @Column(name = "name")
    private String name;

    @Column(name = "primary_email")
    private String email;

    @Column(name = "primary_phone_no")
    private String phone;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", name = "skills")
    private List<String> skills;

    @Column(name = "current_employer")
    private String currentEmployer;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", name = "past_employers")
    private List<String> pastEmployers;

    @Column(name = "experience_in_months")
    private Integer experienceInMonths;

    private String address;

    private String qualification;

    private String certification;

    private String experience;

    private String achievements;

    private String hobbies;

    private String currentDesignation;

    private String almaMater;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "raw_data")
    private Object rawData;
}
