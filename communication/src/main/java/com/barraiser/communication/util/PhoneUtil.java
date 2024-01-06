/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Component;

@Component
public class PhoneUtil {
	public String sanitizePhoneFormat(final String phone) {
		final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			final Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phone, "IN");
			return swissNumberProto.toString();
		} catch (final NumberParseException e) {
			System.err.println("NumberParseException was thrown: " + e.toString());
		}
		return null;
	}

	public boolean isValidPhoneNumber(String recipientPhoneNumber, int phoneCountryCode, int phoneNumberLength) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		Phonenumber.PhoneNumber parsedPhoneNumber;
		try {
			parsedPhoneNumber = phoneUtil.parse(recipientPhoneNumber, null);
		} catch (NumberParseException e) {
			throw new IllegalArgumentException(String.format("Phone number %s unable to parse", recipientPhoneNumber));
		}
		int countryCode = parsedPhoneNumber.getCountryCode();
		return countryCode == phoneCountryCode
				&& String.valueOf(parsedPhoneNumber.getNationalNumber()).length() == phoneNumberLength;
	}
}
