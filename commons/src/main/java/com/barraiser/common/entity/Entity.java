package com.barraiser.common.entity;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Entity {
    private final EntityType type;

    private final String id;

    private final String partnerId;
}
