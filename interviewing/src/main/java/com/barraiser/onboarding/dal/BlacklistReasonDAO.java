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
@Table(name = "blacklist_reason")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BlacklistReasonDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "category")
    private String category;

    @Column(name = "default_blacklist_period_in_days")
    private Integer defaultBlacklistPeriodInDays;

}
