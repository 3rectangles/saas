package com.barraiser.onboarding.interview.certificate;
import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "certificate_details")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CertificateDAO {
    @Id
    private String id;

    @Column(name = "evaluation_id")
    private String evaluationId;

    @Column(name = "image_url")
    private String imageUrl;

}
