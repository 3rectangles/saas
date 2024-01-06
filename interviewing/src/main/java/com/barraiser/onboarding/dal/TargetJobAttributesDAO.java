package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "target_job_attribute")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class TargetJobAttributesDAO extends BaseModel {
    @Id
    private String userId;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", name = "companies")
    private List<String> companies;

    private String desiredRole;

    private String timeToStartApplications;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", name = "skillsToFocus")
    private List<String> skillsToFocus;
}
