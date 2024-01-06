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

@Entity
@Table(name = "general_enquiry")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GeneralEnquiryDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "topic")
    private String topic;

    @Column(name = "message")
    private String message;

    @Column(name = "organisation")
    private String organisation;

    @Column(name = "domain")
    private String domain;

    @Column(name = "linkedin")
    private String linkedIn;

    @Column(name = "reference")
    private String reference;

    @Column(name = "user_identity")
    private String userIdentity;

    @Column(name = "url_params")
    @Type(type = "jsonb")
    private Object urlParams;
}
