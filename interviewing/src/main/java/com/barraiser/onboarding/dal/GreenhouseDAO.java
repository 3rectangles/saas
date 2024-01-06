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
@Table(name = "greenhouse")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GreenhouseDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "evaluation_id")
    private String evaluationId;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "update_status_url")
    private String updateStatusUrl;
}

