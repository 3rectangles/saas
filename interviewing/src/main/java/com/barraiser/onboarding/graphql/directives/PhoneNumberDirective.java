package com.barraiser.onboarding.graphql.directives;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberDirective implements SchemaDirectiveWiring {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    @Override
    public GraphQLInputObjectField onInputObjectField(SchemaDirectiveWiringEnvironment<GraphQLInputObjectField> environment) {
        GraphQLFieldsContainer container = environment.getFieldsContainer();

        final DataFetcher originalFetcher = environment.getFieldDataFetcher();

        final DataFetcher dataFetcher = DataFetcherFactories.wrapDataFetcher(originalFetcher, ((dataFetchingEnvironment, value) -> {
            final Phonenumber.PhoneNumber inputPhone;
            try {
                inputPhone = phoneNumberUtil.parse((String) value, "IN");
                final String newPhoneNumber = phoneNumberUtil.format(inputPhone, PhoneNumberUtil.PhoneNumberFormat.E164);
                return newPhoneNumber;
            } catch (NumberParseException e) {
                throw new IllegalArgumentException("Bad phone number format.");
            }
        }));
        environment.getCodeRegistry().dataFetcher(container, environment.getFieldDefinition(), dataFetcher);

        return environment.getElement();
    }
}
