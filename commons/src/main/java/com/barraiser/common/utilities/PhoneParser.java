package com.barraiser.common.utilities;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PhoneParser {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String getFormattedPhone(final String phone) {
        try {
            final Phonenumber.PhoneNumber phoneNumber = this.phoneNumberUtil.parse(phone, "IN");
            return this.phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (final NumberParseException e) {
            log.warn("Bad phone number format. {}, {}", phone, e);
        }
        return null;
    }
}
