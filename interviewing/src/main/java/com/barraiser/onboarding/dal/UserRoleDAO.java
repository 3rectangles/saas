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
import java.time.Instant;

@Entity
@Table(name = "user_role")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserRoleDAO extends BaseModel {

    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "role")
    private String role;

    @Column(name = "deleted_on")
    private Instant deletedOn;

}
