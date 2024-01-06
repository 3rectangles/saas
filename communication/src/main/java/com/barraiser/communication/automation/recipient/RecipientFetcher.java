package com.barraiser.communication.automation.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.constants.RecipientType;

public interface RecipientFetcher<T> {
    RecipientType getRecipientType();

    EntityType getEntityType();

    T getRecipient(final Entity entity, final String eventType);
}
