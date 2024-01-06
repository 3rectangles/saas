package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "modified_weightages")
@SuperBuilder(toBuilder = true)
public class ModifiedWeightageDAO extends BaseModel {
    @Id
    String Id;

    String difficulty;

    Double rating;

    @Column(name = "modified_weightage")
    Double weightage;
}
