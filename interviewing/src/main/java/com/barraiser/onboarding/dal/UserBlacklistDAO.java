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
@Table(name = "user_blacklist")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserBlacklistDAO extends BaseModel {

    @Id private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "granularity")
    private String granularity;

    @Column(name = "reason_id")
    private String reasonId;

    @Column(name = "partner_company_id")
    private String partnerCompanyId;

    @Column(name = "blacklist_start_date")
    private Instant blacklistStartDate;

    @Column(name = "blacklist_end_date")
    private Instant blacklistEndDate;

    private String createdBy;
}
