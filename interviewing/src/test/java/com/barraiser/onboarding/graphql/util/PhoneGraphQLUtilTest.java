/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql.util;

import com.barraiser.communication.util.PhoneUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.junit.Test;
import java.time.OffsetDateTime;
import static org.junit.Assert.assertEquals;

public class PhoneGraphQLUtilTest {

	@Test
	public void sanitizePhoneFormat() {
		System.out.println(new PhoneUtil().sanitizePhoneFormat("+919538275444"));
		System.out.println(new PhoneUtil().sanitizePhoneFormat("+91-9538275444"));
		System.out.println(new PhoneUtil().sanitizePhoneFormat("+91-95382-75444"));
		System.out.println(new PhoneUtil().sanitizePhoneFormat("95382-75444"));
		System.out.println(new PhoneUtil().sanitizePhoneFormat("9538275444"));

	}

	@Test
	public void javaTest() {
		System.out.println(OffsetDateTime.now().toString());
	}

	private boolean isPhoneNumberValidTest(String recipientPhoneNumber, int countryCodeIndia) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		Phonenumber.PhoneNumber parsedPhoneNumber;
		try {
			parsedPhoneNumber = phoneUtil.parse(recipientPhoneNumber, null);
		} catch (NumberParseException e) {
			return false;
		}
		int countryCode = parsedPhoneNumber.getCountryCode();
		return countryCode == countryCodeIndia && String.valueOf(parsedPhoneNumber.getNationalNumber()).length() == 10;
	}

	@Test
	void isValidPhoneNumber() {
		int countryCodeIndia = 91;
		String recipientPhoneNumber = null;

		recipientPhoneNumber = "+19999999990";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "+919999999990";
		assertEquals(true, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "9999";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "+91";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "+9112345";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "9999999990";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "999pa9999990";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

		recipientPhoneNumber = "-919999999990";
		assertEquals(false, this.isPhoneNumberValidTest(recipientPhoneNumber, countryCodeIndia));

	}
}
