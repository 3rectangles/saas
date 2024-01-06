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
@Table(name = "company")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CompanyDAO extends BaseModel {
    @Id
    private String id;
    private String name;
    private String url;
    private String logo;
    private String industry;
    private String size;

    @Column(name = "send_client_mail")
    private Boolean sendClientMail;
}


