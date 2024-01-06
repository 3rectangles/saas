package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.Money;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cart_item")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CartItemDAO {
    @Id
    private String id;
    private String userId;
    private String itemId;

    /**
     * Type of the item in cart. At this time, its only interview that we are charging. But you never know what else
     * could we be selling.
     */
    @Builder.Default
    private String type = "interview";

    /**
     * Number of items, again, this is highly unlikely to have more than one item, but good to keep it for future.
     */
    @Builder.Default
    private Integer count = 1;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "price")
    private Money price;
}

