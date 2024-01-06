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
import java.io.Serializable;

@Entity
@Table(name = "status_order")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StatusOrderDAO extends BaseModel implements Serializable {
    @Id
    private String id;

    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "order_index")
    private Integer orderIndex;
}
