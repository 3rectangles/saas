/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms;

import com.barraiser.communication.automation.channels.sms.dto.SmsData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class SmsSender implements CommunicationProcessor<SmsData> {

	// public static final String AWS_SNS_SMS_KEY_SENDER_ID =
	// "AWS.SNS.SMS.SenderID";
	// public static final String AWS_SNS_SMS_KEY_MAX_PRICE =
	// "AWS.SNS.SMS.MaxPrice";
	// public static final String AWS_SNS_SMS_KEY_SMS_TYPE = "AWS.SNS.SMS.SMSType";
	//
	// private final AmazonSNS amazonSNS;
	// private final CommunicationStaticAppConfig staticAppConfig;
	// private final PhoneUtil phoneUtil;

	@Override
	public Channel getChannel() {
		return Channel.SMS;
	}

	@Override
	public void process(SmsData data) {

		// final Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
		// smsAttributes.put(AWS_SNS_SMS_KEY_SENDER_ID, new MessageAttributeValue()
		// .withStringValue(staticAppConfig.getAwsSnsSmsSenderId())
		// .withDataType("String"));
		// smsAttributes.put(AWS_SNS_SMS_KEY_MAX_PRICE, new MessageAttributeValue()
		// .withStringValue(staticAppConfig.getAwsSnsSmsMaxPrice())
		// .withDataType("Number"));
		// smsAttributes.put(AWS_SNS_SMS_KEY_SMS_TYPE, new MessageAttributeValue()
		// .withStringValue(staticAppConfig.getAwsSnsSmsType())
		// .withDataType("String"));
		//
		// String recipientPhoneNumber = data.getSmsRecipient().getToPhoneNumber();
		// if (!phoneUtil.isValidPhoneNumber(recipientPhoneNumber,
		// staticAppConfig.getPhoneCountryCodeIndia(),
		// staticAppConfig.getPhoneLengthIndia())) {
		// log.warn(String.format("SMS for non Indian number %s is not supported yet",
		// recipientPhoneNumber));
		// return;
		// }
		//
		// final PublishResult response = this.amazonSNS.publish(new PublishRequest()
		// .withMessage(data.getSmsMessage().getBody())
		// .withPhoneNumber(recipientPhoneNumber)
		// .withMessageAttributes(smsAttributes));
		log.info("No op SMS sender");
	}

}
