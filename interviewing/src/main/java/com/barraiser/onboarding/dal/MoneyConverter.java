package com.barraiser.onboarding.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.barraiser.common.dal.Money;
import com.barraiser.onboarding.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class MoneyConverter implements DynamoDBTypeConverter<String, Money> {
    private final ObjectMapper objectMapper = new AppConfig().getObjectMapper();

    @SneakyThrows
    @Override
    public String convert(final Money object) {
        return this.objectMapper.writeValueAsString(object);
    }

    @Override
    public Money unconvert(final String object) {
        try {
            return this.objectMapper.readValue(object, Money.class);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Data cannot be deserialized.");
        }
    }
}
