/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.security;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@Log4j2
@AllArgsConstructor
public class DataSecurityManager {
	private static final String KEY_ARN = "arn:aws:kms:ap-south-1:969111487786:key/fa0de9ae-473d-4c32-b0e1-5e9683bff4e1";

	private final AWSKMS awskms;

	public byte[] encrypt(final String data) {
		ByteBuffer plaintext = ByteBuffer.wrap(data.getBytes());

		EncryptRequest encryptRequest = new EncryptRequest()
				.withKeyId(KEY_ARN)
				.withPlaintext(plaintext);

		ByteBuffer ciphertext = awskms
				.encrypt(encryptRequest)
				.getCiphertextBlob();

		return ciphertext.array();
	}

	public String decrypt(final byte[] encryptedData) {
		ByteBuffer ciphertextBlob = ByteBuffer.wrap(encryptedData);

		DecryptRequest req = new DecryptRequest()
				.withCiphertextBlob(ciphertextBlob);

		ByteBuffer plainText = awskms
				.decrypt(req)
				.getPlaintext();

		return new String(plainText.array());
	}
}
