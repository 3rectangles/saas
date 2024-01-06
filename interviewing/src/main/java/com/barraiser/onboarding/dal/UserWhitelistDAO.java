package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_whitelist")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserWhitelistDAO extends BaseModel {
    @Id private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "partner_company_id")
    private String partnerCompanyId;

    @Column(name = "whitelist_start_date")
    private Instant whitelistStartDate;

    @Column(name = "whitelist_end_date")
    private Instant whitelistEndDate;

    private String createdBy;
}
