package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.payment.PaymentStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Table(name = "payment")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PaymentDAO extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String userId;

    private String paymentId;

    private String orderId;

    private Double amount;

    private String currency;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<InterviewDAO> interviews;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String type;

    private String typeId;

    private String razorpayPaymentId;
}
