package com.common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import io.minio.ServerSideEncryptionCustomerKey;

public class SecurityUtils {
	public static ServerSideEncryptionCustomerKey getServerSideKey()
			throws InvalidKeyException, NoSuchAlgorithmException {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(256);
			ServerSideEncryptionCustomerKey ssec = new ServerSideEncryptionCustomerKey(keyGen.generateKey());
			return ssec;
		} catch (InvalidKeyException e) {
			throw e;
		} catch (NoSuchAlgorithmException e) {
			throw e;
		}
	}
}
